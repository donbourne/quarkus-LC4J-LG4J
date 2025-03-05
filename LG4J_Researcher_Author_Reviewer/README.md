## Researcher - Author - Reviewer

This is a prototype showing how to use LangGraph4J as the agent orchestrator for a research query app. The app uses 4 agents:

- Researcher - to dig up information about a topic
- Author - to write a blog based on the research provided by the researcher
- Reviewer - to evaluate if the blog needs more research or editing
- Formatter - to format the blog

![image](https://github.ibm.com/maui/exploration/assets/7766/f49231be-8d55-47dc-a6de-0b105938154d)

### Build and Run in Dev Mode
```
mvn quarkus:dev
```

### Making Requests
With the server running, make a request as follows:
http://localhost:8080/research2/<topic+for+a+blog>

### Example Output
http://localhost:8080/research2/impact%20of%20social%20media%20on%20elections

<img width="1727" alt="image" src="https://github.ibm.com/maui/exploration/assets/7766/98eb39a4-3099-48cf-830c-25fc02aeac60">

