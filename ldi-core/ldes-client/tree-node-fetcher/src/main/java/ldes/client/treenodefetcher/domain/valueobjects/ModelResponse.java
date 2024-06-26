package ldes.client.treenodefetcher.domain.valueobjects;

import be.vlaanderen.informatievlaanderen.ldes.ldi.timestampextractor.TimestampExtractor;
import ldes.client.treenodefetcher.domain.entities.TreeMember;
import org.apache.jena.graph.TripleBoundary;
import org.apache.jena.rdf.model.*;

import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.List;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

import static ldes.client.treenodefetcher.domain.valueobjects.Constants.*;

/**
 * Wrapper around the RDF response to more easily extract the required information from that response
 */
public class ModelResponse {
	private final TimestampExtractor timestampExtractor;
	private final ModelExtract modelExtract = new ModelExtract(new StatementTripleBoundary(TripleBoundary.stopNowhere));
	private final Model model;

	public ModelResponse(Model model, TimestampExtractor timestampExtractor) {
		this.model = model;
		this.timestampExtractor = timestampExtractor;
	}

	public List<String> getRelations() {
		return extractRelations()
				.map(relationStatement -> relationStatement.getResource()
						.getProperty(W3ID_TREE_NODE).getResource().toString())
				.toList();
	}

	public List<TreeMember> getMembers() {
		return extractMembers()
				.map(memberStatement -> processMember(model, memberStatement))
				.toList();
	}

	private Stream<Statement> extractMembers() {
		StmtIterator memberIterator = model.listStatements(ANY_RESOURCE, W3ID_TREE_MEMBER, ANY_RESOURCE);

		return Stream.iterate(memberIterator, Iterator::hasNext, UnaryOperator.identity())
				.map(Iterator::next);
	}

	private TreeMember processMember(Model treeNodeModel, Statement memberStatement) {
		final Model memberModel = modelExtract.extract(memberStatement.getObject().asResource(), treeNodeModel);
		final String id = memberStatement.getObject().toString();
		final LocalDateTime createdAt = timestampExtractor.extractTimestampWithSubject(ResourceFactory.createProperty(id), memberModel);
		return new TreeMember(id, createdAt, memberModel);
	}

	private Stream<Statement> extractRelations() {
		return Stream.iterate(model.listStatements(ANY_RESOURCE, W3ID_TREE_RELATION, ANY_RESOURCE),
				Iterator::hasNext, UnaryOperator.identity()).map(Iterator::next);
	}
}
