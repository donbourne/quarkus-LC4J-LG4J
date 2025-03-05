package org.bsc.quarkus.impl;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.RequestScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Singleton;
import org.bsc.langgraph4j.GraphStateException;
import org.bsc.langgraph4j.StateGraph;
import org.bsc.langgraph4j.action.EdgeAction;
import org.bsc.langgraph4j.state.AgentState;
import org.bsc.quarkus.LangGraphFlow;

import java.util.Map;
import java.util.Arrays;
import java.util.stream.Collectors;

import static org.bsc.langgraph4j.StateGraph.END;
import static org.bsc.langgraph4j.StateGraph.START;
import static org.bsc.langgraph4j.action.AsyncEdgeAction.edge_async;
import static org.bsc.langgraph4j.action.AsyncNodeAction.node_async;

@RequestScoped
public class LangGraphFlowA3 {

    private static boolean useLLM = true;

    private LangGraphFlow flow;
    private final ResearcherAIService researcherAIService;
    private final AuthorAIService authorAIService;
    private final ReviewerAIService reviewerAIService;
    private final FormatterAIService formatterAIService;

    public LangGraphFlowA3(ResearcherAIService researcherAIService, 
                           AuthorAIService authorAIService, 
                           ReviewerAIService reviewerAIService, 
                           FormatterAIService formatterAIService) {
        this.researcherAIService = researcherAIService;
        this.authorAIService = authorAIService;
        this.reviewerAIService = reviewerAIService;
        this.formatterAIService = formatterAIService;
    }

    @PostConstruct
    void init() throws GraphStateException {
        flow = sampleFlow();
    }
    @Produces
    public LangGraphFlow getFlow()  {
        return flow;
    }

    private LangGraphFlow sampleFlow() throws GraphStateException {

        final EdgeAction<AgentState> reviewerConditionalEdge = new EdgeAction<>() {
            int steps= 0;
            @Override
            public String apply(AgentState state) {
                steps++;
                if (steps >= 3) {
                    System.out.println("Maximum number of reviews already complete.");
                    return "formatter";
                }

                String review_comments = (String)state.value("review_comments").orElse("");
                if (review_comments.contains("RESEARCHER")) {
                    System.out.println("forwarding to researcher.");
                    return "researcher";
                }
                else if (review_comments.contains("AUTHOR")){
                    System.out.println("forwarding to author.");
                    return "author";
                }
                else {
                    System.out.println("forwarding to formatter.");
                    return "formatter";
                }
            }
        };

        final EdgeAction<AgentState> researcherConditionalEdge = new EdgeAction<>() {
            @Override
            public String apply(AgentState state) {
                String research_bullets = (String)state.value("research_bullets").orElse("");
                if (research_bullets.contains("getFacts")) {
                    // tool call was expected but didn't happen
                    System.out.println("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
                    System.out.println("tool call expected but didn't occur. retrying.");
                    System.out.println("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
                    return "redo";
                }
                else {
                    return "done";
                }
            }
        };

        var workflow = new StateGraph<>(AgentState::new)
                .addNode("researcher", node_async((state) -> {
                    System.out.println("---------- RESEARCHER ----------");

                    String topic = (String)state.value("topic").orElse("");
                    // System.out.println("topic: " + topic);

                    String previous_research_bullets = (String)state.value("research_bullets").orElse("");
                    // System.out.println("previous research: " + previous_research_bullets);
                    
                    String review_comments = (String)state.value("review_comments").orElse("");
                    review_comments = getLinesWithPrefix(review_comments, "RESEARCHER", "");
                    // System.out.println("review_comments: " + review_comments);
                    
                    String new_research_bullets = useLLM ? researcherAIService.doResearch(topic, previous_research_bullets, review_comments) 
                    : "no research";
                    System.out.println("research_bullets: " + new_research_bullets);

                    return Map.of("research_bullets", new_research_bullets);
                }))
                .addNode("author", node_async((state) -> {
                    System.out.println("---------- AUTHOR ----------");
                    
                    String topic = (String)state.value("topic").orElse("");
                    // System.out.println("topic: " + topic);

                    String research = (String)state.value("research_bullets").orElse("");
                    // System.out.println("research: " + research);

                    String previous_article = (String)state.value("article").orElse("");
                    // System.out.println("previous_article: " + previous_article);

                    String review_comments = (String)state.value("review_comments").orElse("");
                    review_comments = getLinesWithPrefix(review_comments, "AUTHOR", "");
                    // System.out.println("review_comments: " + review_comments);

                    String article = useLLM ? authorAIService.writeBlog(topic, research, previous_article, review_comments) 
                    : "my article";
                    System.out.println("article: " + article);

                    return Map.of("article", article);
                }))
                .addNode("reviewer", node_async((state) -> {
                    System.out.println("---------- REVIEWER ----------");
                    
                    String topic = (String)state.value("topic").orElse("");
                    // System.out.println("topic: " + topic);

                    String article = (String)state.value("article").orElse("");
                    // System.out.println("article: " + article);

                    String review_comments = useLLM ? reviewerAIService.reviewBlog(topic, article) : "";
                    System.out.println("review comments: " + review_comments);

                    return Map.of("review_comments", review_comments);
                }))
                .addNode("formatter", node_async((state) -> {
                    System.out.println("---------- FORMATTER ----------");

                    String article = (String)state.value("article").orElse("");
                    // System.out.println("article: " + article);

                    String formatted_article = useLLM ? formatterAIService.format(article, "format in HTML, with a cool banner at the top introducing the blog. Pay attention to spacing. Replace asterisks with HTML formatting as appropriate.") 
                    : "formatted article";
                    System.out.println("formatted_article: " + formatted_article);

                    try {
                        Thread.sleep(1); // sleep for 1 second
                    } catch (InterruptedException e) {
                    }

                    return Map.of("formatted_article", formatted_article);
                }))
                .addEdge(START, "researcher")
                .addEdge("author", "reviewer")
                .addEdge("formatter", END)
                .addConditionalEdges("reviewer", edge_async(reviewerConditionalEdge), Map.of("author", "author", "researcher", "researcher", "formatter", "formatter"))
                .addConditionalEdges("researcher", edge_async(researcherConditionalEdge), Map.of("redo", "researcher", "done", "author"));
                


        return  LangGraphFlow.builder()
                .title("LangGraph A3 Example")
                .addInputStringArg("topic")
                .stateGraph( workflow )
                .build();
    }

    // Assisted by watsonx Code Assistant 
    public String getLinesWithPrefix(String multiLineString, String search, String defaultResult) {
        String result = Arrays.stream(multiLineString.split("\n"))
            .filter(line -> line.contains(search))
            .collect(Collectors.joining("\n"));
        if (result == null || result.isEmpty())
            result = defaultResult;
        return result;
    }
}
