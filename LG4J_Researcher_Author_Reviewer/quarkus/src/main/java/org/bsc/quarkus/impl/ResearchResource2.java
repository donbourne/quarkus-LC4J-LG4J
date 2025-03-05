package org.bsc.quarkus.impl;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import dev.langchain4j.data.message.UserMessage;
//import org.bsc.langgraph4j.NodeOutput;
//import org.bsc.langgraph4j.agentexecutor.AgentExecutor;
import org.bsc.quarkus.LangGraphFlow;
import org.bsc.langgraph4j.StateGraph;
import org.bsc.langgraph4j.CompiledGraph;
import org.bsc.langgraph4j.state.AgentState;


@Path("/research2")
public class ResearchResource2 {

    private final LangGraphFlow flow;

    public ResearchResource2(LangGraphFlow flow) {
        this.flow = flow;
    }

    @GET
    @Path("/{topic}")
    @Produces(MediaType.TEXT_HTML)
    public String researchTopic(@PathParam("topic") String topic) {
        System.out.println("researchTopic: " + topic);
        
        String formatted_article = null;

        try {
            StateGraph sg = flow.stateGraph();
            CompiledGraph c = sg.compile();
            Optional<AgentState> i = c.invoke(Map.of( "topic", topic));
            AgentState as = (AgentState)i.get();
            formatted_article = (String)as.value("formatted_article").get();
            System.out.println("========== FLOW COMPLETE ==========");
            System.out.println("formatted_article: " + formatted_article);

            // article = (String)flow.stateGraph().compile().invoke(Map.of("messages", topic)).get().value("article").get();
        }
        catch (Exception e) {
            System.out.println("exception: " + e);
        }

        return formatted_article;
    }
}