package toast.ui.controller;

import javafx.fxml.Initializable;

public abstract class PageController implements Initializable {

    /**
     * 해당 페이지(Pane)이 생성될 때의 동작을 입력한다.
     */
    abstract void init();
    /**
     * 해당 페이지(Pane)이 종료될 때의 동작을 입력한다.
     */
    abstract void exit();
}
