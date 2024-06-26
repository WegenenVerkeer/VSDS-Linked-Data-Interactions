package be.vlaanderen.informatievlaanderen.ldes.ldi.postgres;

import be.vlaanderen.informatievlaanderen.ldes.ldi.EntityManagerFactory;

import javax.persistence.EntityManager;
import javax.persistence.Persistence;
import java.util.HashMap;
import java.util.Map;

public class PostgresEntityManagerFactory implements EntityManagerFactory {

	public static final String PERSISTENCE_UNIT_NAME = "pu-postgres-jpa";
	private static final Map<String, PostgresEntityManagerFactory> instances = new HashMap<>();
	private final EntityManager em;
	private final javax.persistence.EntityManagerFactory emf;

	private PostgresEntityManagerFactory(Map<String, String> properties) {
		emf = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME, properties);
		em = emf.createEntityManager();
	}

	public static synchronized PostgresEntityManagerFactory getInstance(String instanceName, Map<String, String> properties) {
		return instances.computeIfAbsent(instanceName, s -> new PostgresEntityManagerFactory(properties));
	}

	@Override
	public EntityManager getEntityManager() {
		return em;
	}

	@Override
	public void destroyState(String instanceName) {
		em.close();
		emf.close();
		instances.remove(instanceName);
		// DELETE tables
	}
}
