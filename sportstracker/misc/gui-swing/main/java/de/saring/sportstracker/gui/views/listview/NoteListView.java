package de.saring.sportstracker.gui.views.listviews;

import de.saring.sportstracker.core.STException;
import de.saring.sportstracker.data.Note;
import de.saring.sportstracker.gui.STController;
import de.saring.util.data.IdObject;

import javax.inject.Singleton;
import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumnModel;
import java.time.LocalDateTime;

/**
 * This view class displays the list of the users notes in a table view.
 *
 * @author Stefan Saring
 * @version 1.0
 */
@Singleton
public class NoteListView extends BaseListView {

    /**
     * Constants for columns indexes.
     */
    private static final int COLUMN_COUNT = 2;
    private static final int COLUMN_DATE = 0;
    private static final int COLUMN_TEXT = 1;
    private static final int HIDDEN_COLUMN_ID = 2;

    private String[] columnNames;

    /**
     * The table model of the notes table.
     */
    private NotesTableModel tmNotes;


    @Override
    public void initView() {

        // initialize array of column names
        columnNames = new String[COLUMN_COUNT];
        columnNames[0] = getContext().getResReader().getString("st.notelistview.date");
        columnNames[1] = getContext().getResReader().getString("st.notelistview.text");

        // create tablemodel
        tmNotes = new NotesTableModel();

        // create table and setup its context menu
        createTable();
        getPopupMenu().insert(getController().getActionMap().get(STController.ACTION_NOTE_ADD), 0);

        // set column widths 
        getTable().setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
        TableColumnModel tcModel = getTable().getColumnModel();
        tcModel.getColumn(COLUMN_DATE).setWidth(80);
        tcModel.getColumn(COLUMN_TEXT).setPreferredWidth(600);
    }

    @Override
    protected String[] getColumnNames() {
        return columnNames;
    }

    @Override
    protected AbstractTableModel getTableModel() {
        return tmNotes;
    }

    @Override
    protected int getIdColumnIndex() {
        return HIDDEN_COLUMN_ID;
    }

    @Override
    public int getSelectedNoteCount() {
        return getSelectedEntryCount();
    }

    @Override
    public int[] getSelectedNoteIDs() {
        return getSelectedEntryIDs();
    }

    @Override
    public void selectEntry(IdObject entry) {
        if (entry instanceof Note) {
            selectRowWithEntryId(entry.getId());
        }
    }

    @Override
    public void print() throws STException {
        if (!getController().checkForExistingNotes()) {
            return;
        }

        printList(
                getContext().getResReader().getString("st.notelistview.print.title"),
                getContext().getResReader().getString("st.notelistview.print.page"));
    }

    /**
     * Table model implementation for the Notes table.
     */
    class NotesTableModel extends BaseListView.BaseListTableModel {

        /**
         * Returns the number of notes.
         */
        @Override
        public int getRowCount() {
            return getDocument().getNoteList().size();
        }

        /**
         * Returns the class of the specified column (needed for sorting).
         */
        @Override
        public Class<?> getColumnClass(int col) {
            switch (col) {
                case COLUMN_DATE:
                    return LocalDateTime.class;
                default:
                    return Object.class;
            }
        }

        /**
         * Returns the exercise value for the specified column.
         */
        @Override
        public Object getValueAt(int row, int col) {
            Note note = getDocument().getNoteList().getAt(row);

            switch (col) {
                case COLUMN_DATE:
                    return note.getDateTime();
                case COLUMN_TEXT:
                    return getContext().getFormatUtils().firstLineOfText(note.getText());
                case HIDDEN_COLUMN_ID:
                    return note.getId();
            }
            return null;
        }
    }
}
