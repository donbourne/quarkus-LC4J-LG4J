package org.bsc.quarkus.impl;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;

@Path("/research")
public class ResearchResource {

    private final ResearcherAIService service;

    public ResearchResource(ResearcherAIService service) {
        this.service = service;
    }

    @GET
    @Path("/{topic}")
    public String researchTopic(@PathParam("topic") String topic) {
        return service.doResearch(topic, "", "");
    }

}