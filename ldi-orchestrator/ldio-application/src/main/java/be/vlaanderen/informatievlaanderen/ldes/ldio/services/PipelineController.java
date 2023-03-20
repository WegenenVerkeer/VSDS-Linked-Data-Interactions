package be.vlaanderen.informatievlaanderen.ldes.ldio.services;

import be.vlaanderen.informatievlaanderen.ldes.ldio.events.PipelineStatusEvent;
import be.vlaanderen.informatievlaanderen.ldes.ldio.valueobjects.PipelineStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static be.vlaanderen.informatievlaanderen.ldes.ldio.valueobjects.PipelineStatus.*;

@RestController
@RequestMapping(path = "/admin/api/v1/pipeline")
public class PipelineController {

	@Autowired
	private ApplicationEventPublisher applicationEventPublisher;

	private PipelineStatus pipelineStatus = PipelineStatus.RUNNING;

	@GetMapping(path = "/status")
	ResponseEntity<PipelineStatus> getPipelineStatus() {
		return ResponseEntity.ok(pipelineStatus);
	}

	@PostMapping(path = "/halt")
	ResponseEntity<PipelineStatus> haltPipeline() {
		return switch (pipelineStatus) {
			case RUNNING, RESUMING -> haltRunningPipeline();
			case HALTED -> ResponseEntity.ok(HALTED);
		};
	}

	@PostMapping(path = "/resume")
	ResponseEntity<PipelineStatus> resumePipeline() {
		return switch (pipelineStatus) {
			case RUNNING -> ResponseEntity.ok(RUNNING);
			case HALTED -> resumeHaltedPipeline();
			case RESUMING -> ResponseEntity.ok(RESUMING);
		};
	}

	@EventListener
	public void handlePipelineStatusResponse(PipelineStatusEvent statusEvent) {
		this.pipelineStatus = statusEvent.getStatus();
	}

	private ResponseEntity<PipelineStatus> resumeHaltedPipeline() {
		applicationEventPublisher.publishEvent(new PipelineStatusEvent(RESUMING));
		return ResponseEntity.ok(RESUMING);
	}

	private ResponseEntity<PipelineStatus> haltRunningPipeline() {
		applicationEventPublisher.publishEvent(new PipelineStatusEvent(HALTED));
		return ResponseEntity.ok(HALTED);
	}

}