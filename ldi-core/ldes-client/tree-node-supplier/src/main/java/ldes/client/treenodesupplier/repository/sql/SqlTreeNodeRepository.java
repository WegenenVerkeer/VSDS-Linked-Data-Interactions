package ldes.client.treenodesupplier.repository.sql;

import be.vlaanderen.informatievlaanderen.ldes.ldi.EntityManagerFactory;
import be.vlaanderen.informatievlaanderen.ldes.ldi.entities.TreeNodeRecordEntity;
import ldes.client.treenodesupplier.domain.entities.TreeNodeRecord;
import ldes.client.treenodesupplier.domain.valueobject.TreeNodeStatus;
import ldes.client.treenodesupplier.repository.TreeNodeRecordRepository;
import ldes.client.treenodesupplier.repository.mapper.TreeNodeRecordEntityMapper;

import java.util.Optional;

import javax.persistence.EntityManager;

public class SqlTreeNodeRepository implements TreeNodeRecordRepository {

	final EntityManagerFactory entityManagerFactory;
	private final EntityManager entityManager;
	private final String instanceName;

	public SqlTreeNodeRepository(String instanceName, EntityManagerFactory entityManagerFactory) {
		this.entityManagerFactory = entityManagerFactory;
		this.entityManager = entityManagerFactory.getEntityManager();
		this.instanceName = instanceName;
	}

	@Override
	public void saveTreeNodeRecord(TreeNodeRecord treeNodeRecord) {
		TreeNodeRecordEntity memberRecordEntity = TreeNodeRecordEntityMapper.fromTreeNodeRecord(treeNodeRecord);
		entityManager.getTransaction().begin();
		entityManager.merge(memberRecordEntity);
		entityManager.getTransaction().commit();
	}

	@Override
	public boolean existsById(String treeNodeId) {
		return entityManager
				.createNamedQuery("TreeNode.countById", Long.class)
				.setParameter("id", treeNodeId)
				.getSingleResult() > 0;
	}

	@Override
	public Optional<TreeNodeRecord> getTreeNodeRecordWithStatusAndEarliestNextVisit(TreeNodeStatus treeNodeStatus) {
		return entityManager
				.createQuery("SELECT t FROM TreeNodeRecordEntity t" +
								" WHERE t.treeNodeStatus = :treeNodeStatus" +
								" ORDER BY t.earliestNextVisit",
						TreeNodeRecordEntity.class)
				.setParameter("treeNodeStatus", treeNodeStatus.name())
				.getResultStream()
				.findFirst()
				.map(TreeNodeRecordEntityMapper::toTreeNode);
	}

	@Override
	public boolean existsByIdAndStatus(String treeNodeId, TreeNodeStatus treeNodeStatus) {
		return ((Number) entityManager
				.createNamedQuery("TreeNode.countByIdAndStatus")
				.setParameter("id", treeNodeId)
				.setParameter("treeNodeStatus", treeNodeStatus.name())
				.getSingleResult()).longValue() > 0;
	}

	@Override
	public void destroyState() {
		entityManagerFactory.destroyState(instanceName);
	}

	@Override
	public boolean containsTreeNodeRecords() {
		return entityManager
				.createNamedQuery("TreeNode.count", Long.class)
				.getSingleResult() > 0;
	}

	@Override
	public void resetContext() {
		entityManager.clear();
	}

}
