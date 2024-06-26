package ldes.client.treenodefetcher.domain.valueobjects;

import ldes.client.treenodefetcher.domain.entities.TreeMember;

import java.util.List;

/**
 * Wrapper around the RDF response that represents a fragment to more easily extract the required information
 * from that response
 */
public class TreeNodeResponse {
	private final List<String> relation;
	private final List<TreeMember> members;
	private final MutabilityStatus mutabilityStatus;

	public TreeNodeResponse(List<String> relations, List<TreeMember> members,
							MutabilityStatus mutabilityStatus) {
		this.relation = relations;
		this.members = members;
		this.mutabilityStatus = mutabilityStatus;
	}

	public List<String> getRelations() {
		return relation;
	}

	public List<TreeMember> getMembers() {
		return members;
	}

	public MutabilityStatus getMutabilityStatus() {
		return mutabilityStatus;
	}
}
