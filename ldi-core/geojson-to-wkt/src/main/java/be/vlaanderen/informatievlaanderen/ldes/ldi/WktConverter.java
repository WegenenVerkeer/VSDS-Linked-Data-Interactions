package be.vlaanderen.informatievlaanderen.ldes.ldi;

import be.vlaanderen.informatievlaanderen.ldes.ldi.valueobjects.GeoType;
import org.apache.jena.datatypes.RDFDatatype;
import org.apache.jena.datatypes.TypeMapper;
import org.apache.jena.rdf.model.*;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.io.WKTWriter;

import java.util.List;

import static org.apache.jena.rdf.model.ResourceFactory.createProperty;

public class WktConverter {

	public static final String GEOSPARQL_URI = "http://www.opengis.net/ont/geosparql";
	public static final Property GEOJSON_GEOMETRY = createProperty("https://purl.org/geojson/vocab#geometry");

	private final GeometryExtractor geometryExtractor = new GeometryExtractor();
	private final WKTWriter writer = new WKTWriter();

	/**
	 * Locates the geojson:geometry from the provided model and returns the WKT
	 * translation
	 */
	public WktResult getWktFromModel(Model model) {
		final Geometry geom = geometryExtractor.createGeometry(model, getGeometryId(model));
		GeoType geoType = GeoType.fromName(geom.getGeometryType())
				.orElseThrow(() -> new IllegalArgumentException("Geotype %s not supported".formatted(geom.getGeometryType())));
		return new WktResult(geoType, writer.write(geom));
	}

	public Literal getWktLiteral(WktResult wktResult) {
		RDFDatatype wktDataType = TypeMapper.getInstance().getSafeTypeByName(GEOSPARQL_URI + "#wktLiteral");
		return ResourceFactory.createTypedLiteral(wktResult.wkt(),wktDataType);
	}

	private Resource getGeometryId(Model model) {
		final List<Resource> geometryStatements = model.listObjectsOfProperty(GEOJSON_GEOMETRY)
				.mapWith(RDFNode::asResource).toList();

		if (geometryStatements.size() > 1) {
			throw new IllegalArgumentException("Ambiguous request: multiple geojson:geometry found.");
		}

		return geometryStatements.getFirst();
	}

}
