package de.saring.sportstracker.gui.dialogs;

import javafx.fxml.FXML;
import javafx.scene.control.Control;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Window;
import javafx.util.converter.NumberStringConverter;

import javax.inject.Inject;

import org.controlsfx.validation.ValidationResult;
import org.controlsfx.validation.Validator;

import de.saring.sportstracker.data.Weight;
import de.saring.sportstracker.gui.STContext;
import de.saring.sportstracker.gui.STDocument;
import de.saring.util.ValidationUtils;

/**
 * Controller (MVC) class of the Wieght dialog for editing / adding Weight entries.
 *
 * @author Stefan Saring
 */
public class WeightDialogController extends AbstractDialogController {

    private final STDocument document;

    @FXML
    private DatePicker dpDate;

    // TODO use formatted TextField, will be introduced in JavaFX 8u40
    @FXML
    private TextField tfHour;

    // TODO use formatted TextField, will be introduced in JavaFX 8u40
    @FXML
    private TextField tfMinute;

    @FXML
    private TextField tfValue;

    @FXML
    private Label laWeightUnit;

    @FXML
    private TextArea taComment;

    /** ViewModel of the edited Weight. */
    private WeightViewModel weightViewModel;

    /**
     * Standard c'tor for dependency injection.
     *
     * @param context the SportsTracker UI context
     * @param document the SportsTracker model/document
     */
    @Inject
    public WeightDialogController(final STContext context, final STDocument document) {
        super(context);
        this.document = document;
    }

    /**
     * Displays the Weight dialog for the passed Weight instance.
     *
     * @param parent parent window of the dialog
     * @param weight Weight to be edited
     */
    public void show(final Window parent, final Weight weight) {
        this.weightViewModel = new WeightViewModel(weight, document.getOptions().getUnitSystem());

        final boolean newWeight = document.getWeightList().getByID(weight.getId()) == null;
        final String dlgTitleKey = newWeight ? "st.dlg.weight.title.add" : "st.dlg.weight.title";
        final String dlgTitle = context.getResources().getString(dlgTitleKey);

        showEditDialog("/fxml/dialogs/WeightDialog.fxml", parent, dlgTitle);
    }

    @Override
    protected void setupDialogControls() {

        laWeightUnit.setText(context.getFormatUtils().getWeightUnitName());

        // setup binding between view model and the UI controls
        dpDate.valueProperty().bindBidirectional(weightViewModel.date);
        tfHour.textProperty().bindBidirectional(weightViewModel.hour, new NumberStringConverter("00"));
        tfMinute.textProperty().bindBidirectional(weightViewModel.minute, new NumberStringConverter("00"));
        tfValue.textProperty().bindBidirectional(weightViewModel.value, new NumberStringConverter());
        taComment.textProperty().bindBidirectional(weightViewModel.comment);

        // setup validation of the UI controls
        validationSupport.registerValidator(dpDate,
                Validator.createEmptyValidator(context.getResources().getString("st.dlg.weight.error.date")));
        validationSupport.registerValidator(tfHour, true, (Control control, String newValue) ->
                ValidationResult.fromErrorIf(tfHour, context.getResources().getString("st.dlg.weight.error.time"),
                        !ValidationUtils.isValueIntegerBetween(newValue, 0, 23)));
        validationSupport.registerValidator(tfMinute, true, (Control control, String newValue) ->
                ValidationResult.fromErrorIf(tfMinute, context.getResources().getString("st.dlg.weight.error.time"),
                        !ValidationUtils.isValueIntegerBetween(newValue, 0, 59)));
        validationSupport.registerValidator(tfValue, true, (Control control, String newValue) ->
                ValidationResult.fromErrorIf(tfValue, context.getResources().getString("st.dlg.weight.error.weight"),
                        !ValidationUtils.isValueDoubleBetween(newValue, 0.1d, 1000)));
    }

        @Override
        protected boolean validateAndStore() {

        // store the new Weight, no further validation needed
        final Weight newWeight = weightViewModel.getWeight();
        document.getWeightList().set(newWeight);
        return true;
    }
}
