/**
 * 
 * @author Shahar-Y
 */
package gui.driver.app;

//import gui.driver.shaharTesting.ListViewTesting;
//import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class Main {
	static Scene scene;
	Stage window;
	
	
	public static Class<? extends AbstractWindow> getWindowClass(WindowEnum ¢){
		switch (¢) {
		case CHOOSE_ACTION:
			return ChooseAction.class;
		case GET_PASS_BY_MAIL:
			return GetPassByMail.class;
		case MY_DETAILS:
			return MyDetails.class;
		case MY_DETAILS_EDIT:
			return MyDetailsEdit.class;
	//	case OPENING: 
	//		return Opening.class; 
		default:
			return null;
		}
	}

//	public static void main(String[] args) {
//		launch(args);
//	}

//	@Override
//	public void start(Stage primaryStage) throws Exception {
//		
//		(new Opening()).start(primaryStage);
//		
		//	ChooseAction chooseActionObject = new ChooseAction();
		//	ArrayList<AbstractWindow> prevWindows = new ArrayList<AbstractWindow>();
		//	prevWindows.add(chooseActionObject);
		//	chooseActionObject.display(primaryStage, WindowEnum.NONE, prevWindows);
		
//		if (!ConfirmBox.display("Choose Action", "would you like to get password?"))
//			AlertBox.display("Goodbye!", "Hope you enjoyed!");
//		else
//			GetPassByMail.display(primaryStage, WindowEnum.NONE);
		
		
		
		//primaryStage.close();
		//VHBoxesExperiment.display(primaryStage);
	}

//}
