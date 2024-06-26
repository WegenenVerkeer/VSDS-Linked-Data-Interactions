package ldes.client.treenodesupplier.membersuppliers;

import be.vlaanderen.informatievlaanderen.ldes.ldi.VersionMaterialiser;
import ldes.client.treenodesupplier.domain.valueobject.SuppliedMember;
import org.apache.jena.rdf.model.Model;

/**
 * This is a decorator for the {@link MemberSupplier} which makes it possible to materialize the version objects to
 * state objects before supplying them.
 */
public class VersionMaterialisedMemberSupplier extends MemberSupplierDecorator {

	private final VersionMaterialiser versionMaterialiser;

	public VersionMaterialisedMemberSupplier(MemberSupplier memberSupplier, VersionMaterialiser versionMaterialiser) {
		super(memberSupplier);
		this.versionMaterialiser = versionMaterialiser;
	}

	/**
	 * Materializes the supplied member before returning them
	 *
	 * @return the materialized version of the provided base supplied member
	 */
	@Override
	public SuppliedMember get() {
		final SuppliedMember suppliedMember = super.get();
		final Model stateObject = versionMaterialiser.transform(suppliedMember.getModel());
		return new SuppliedMember(suppliedMember.getId(), stateObject);
	}
}
