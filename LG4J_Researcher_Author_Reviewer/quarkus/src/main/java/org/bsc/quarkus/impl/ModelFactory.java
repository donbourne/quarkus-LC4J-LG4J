// import dev.langchain4j.model.chat.ChatLanguageModel;
// import dev.langchain4j.model.ollama.OllamaChatModel;

class ModelFactory {
    static String BASE_URL = "http://localhost:11434";

    // try "mistral", "llama2", "codellama", "phi" or "tinyllama"
    static String MODEL_NAME = "orca-mini"; 

    // public static ChatLanguageModel getModel() {
    //     ChatLanguageModel model = OllamaChatModel.builder()
    //             .baseUrl(BASE_URL)
    //             .modelName(MODEL_NAME)
    //             .build();
    //     return model;
    // }
}
