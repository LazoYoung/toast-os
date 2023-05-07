package toast.persistence.mapper;

import toast.ui.controller.SetUpResult;

public class SetUpMapper {
    private static SetUpResult setUpResult;

    public static void save(SetUpResult setUpResult) {
        SetUpMapper.setUpResult = setUpResult;
    }

    public static SetUpResult get() {
        return setUpResult;
    }
}
