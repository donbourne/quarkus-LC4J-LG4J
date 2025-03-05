## Researcher - Author - Reviewer

This is a prototype showing how to use LangGraph4J as the agent orchestrator for a research query app. The app uses 4 agents:

- Researcher - to dig up information about a topic
- Author - to write a blog based on the research provided by the researcher
- Reviewer - to evaluate if the blog needs more research or editing
- Formatter - to format the blog

### Build and Run in Dev Mode
```
mvn quarkus:dev
```

### To view LangGraph Studio
With the server running, make a request as follows:

http://localhost:8080

<img width="179" alt="image" src="https://github.com/user-attachments/assets/581495c3-fe15-4bfe-ba1d-405cfd347d09" />

### Making Requests
With the server running, make a request as follows:

http://localhost:8080/research2/<topic+for+a+blog>

### Example Output
http://localhost:8080/research2/impact%20of%20social%20media%20on%20elections

![image](https://github.com/user-attachments/assets/2e80e21f-b9af-450a-9337-f2bc21cb8641)

