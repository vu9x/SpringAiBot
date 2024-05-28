package vn.vt.service.enums;

public enum ServiceCommand {

    HELP("/help"),
    REGISTRATION("/registration"),
    CHAT_GPT("/chatgpt"),
    CANCEL("/cancel"),
    START("/start");

    private final String value;

    ServiceCommand(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }

    public static ServiceCommand fromValue(String v){
        for (ServiceCommand command: ServiceCommand.values()){
            if (command.value.equals(v)){
                return command;
            }
        }
        return null;
    }
}
