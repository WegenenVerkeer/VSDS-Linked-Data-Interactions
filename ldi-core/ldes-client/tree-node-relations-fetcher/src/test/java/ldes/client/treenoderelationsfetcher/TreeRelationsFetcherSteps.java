package ldes.client.treenoderelationsfetcher;

import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.services.RequestExecutorFactory;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import ldes.client.treenoderelationsfetcher.domain.valueobjects.TreeNodeRequest;
import ldes.client.treenoderelationsfetcher.domain.valueobjects.TreeRelation;
import org.apache.jena.riot.RDFLanguages;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class TreeRelationsFetcherSteps {
	private final RequestExecutorFactory requestExecutorFactory = new RequestExecutorFactory(false);
	private TreeNodeRequest treeNodeRequest;
	private TreeRelationsFetcher treeRelationsFetcher;
	private List<TreeRelation> treeNodeRelations;

	@Given("I have a TreeRelationsFetcher")
	public void iHaveATreeRelationsFetcher() {
		treeRelationsFetcher = new TreeRelationsFetcher(requestExecutorFactory.createNoAuthExecutor());
	}

	@And("I fetch the TreeNodeRelations")
	public void iFetchTheTreeNodeRelations() {
		treeNodeRelations = treeRelationsFetcher.fetchTreeRelations(treeNodeRequest);
	}

	@Then("the obtained TreeNodeRelation has {int} relations")
	public void theObtainedTreeNodeRelationHasRelations(int numberOfRelations) {
		assertThat(treeNodeRelations).hasSize(numberOfRelations);
	}

	@Then("An UnSupportedOperationException is thrown")
	public void anUnSupportedOperationExceptionIsThrown() {
		final String expectedErrorMessage = "Cannot handle response 404 of TreeNodeRequest TreeNodeRequest{treeNodeUrl='http://localhost:10101/404-not-found', lang=Lang:Turtle}";
		assertThatThrownBy(() -> treeRelationsFetcher.fetchTreeRelations(treeNodeRequest))
				.isInstanceOf(UnsupportedOperationException.class)
				.hasMessage(expectedErrorMessage);
	}

	@When("I create a TreeNodeRequest with Lang {string} and url {string}")
	public void iCreateATreeNodeRequestWithLangAndUrl(String lang, String url) {
		treeNodeRequest = new TreeNodeRequest(url, RDFLanguages.nameToLang(lang));
	}
}
