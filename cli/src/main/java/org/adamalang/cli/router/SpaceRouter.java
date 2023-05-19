package org.adamalang.cli.router;

public interface SpaceRouter {
    default int route(String[] args) {
        switch (args[1]) {
            case "create":
                createSpace("CreateSpaceArgs", "YesOrNo Output");
                break;
            case "destroy":
                deleteSpace("DeleteSpaceArgs", "YesOrNoOutput");
            default:

        }
        return 0;
    }

    int createSpace(String argPlaceHolder, String outputPlaceHolder);
    int deleteSpace(String argPlaceHolder, String outputPlaceHolder);


}
