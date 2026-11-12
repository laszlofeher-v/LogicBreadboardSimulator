package com.logicbreadboard.simulator;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.graphics.Color;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

class FileBrowser {

    private boolean isNewFolderEnabled = true;
    private String sdcardDirectory = "";
    private Context context;
    private TextView titleView;
    private EditText fileNameView;

    private String dir = "";
    private List<String> subdirs = null;
    private ChosenDirectoryListener chosenDirectoryListener = null;
    private CancelListener cancelListener = null;
    private ArrayAdapter<String> listAdapter = null;

    public FileBrowser(Context context, ChosenDirectoryListener chosenDirectoryListener, CancelListener cancelListener) {
        this.context = context;
        sdcardDirectory = Settings.defaultDirectory;
        this.chosenDirectoryListener = chosenDirectoryListener;
        this.cancelListener = cancelListener;

        try {
            sdcardDirectory = new File(sdcardDirectory).getCanonicalPath();
        } catch (IOException ioe) {
        }
    }

    public boolean getNewFolderEnabled() {
        return isNewFolderEnabled;
    }

    public void setNewFolderEnabled(boolean isNewFolderEnabled) {
        this.isNewFolderEnabled = isNewFolderEnabled;
    }

    public void chooseDirectory() {
        chooseDirectory(sdcardDirectory);
    }

    public void chooseDirectory(String dir) {
        File dirFile = new File(dir);
        if (!dirFile.exists() || !dirFile.isDirectory()) {
            dir = sdcardDirectory;
        }
        try {
            dir = new File(dir).getCanonicalPath();
        } catch (IOException ioe) {
            return;
        }

        this.dir = dir;
        subdirs = getDirectories(dir);

        class DirectoryOnClickListener implements DialogInterface.OnClickListener {
            public void onClick(DialogInterface dialog, int item) {
                FileBrowser.this.dir += "/" + ((AlertDialog) dialog).getListView().getAdapter().getItem(item);
                File file = new File(FileBrowser.this.dir);
                if (file.isDirectory()) {
                    updateDirectory();
                } else {
                    if (chosenDirectoryListener.onChosenDir(FileBrowser.this.dir, false)) {
                        dialog.cancel();
                    }
                }
            }
        }
        AlertDialog.Builder dialogBuilder =
                createDirectoryChooserDialog(dir, subdirs, new DirectoryOnClickListener());
        dialogBuilder.setPositiveButton("OK", new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (chosenDirectoryListener != null) {
                    // Call registered listener supplied with the chosen directory
                    FileBrowser.this.dir += "/" + fileNameView.getText().toString();
                    if (chosenDirectoryListener.onChosenDir(FileBrowser.this.dir, false)) {
                        dialog.cancel();
                    }
                }
            }
        });
        dialogBuilder.setNegativeButton("Cancel", new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (cancelListener != null) {
                    cancelListener.onCancel(FileBrowser.this.dir);
                }
            }
        });
        dialogBuilder.setNeutralButton("Overwrite existing", new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (chosenDirectoryListener != null) {
                    // Call registered listener supplied with the chosen directory
                    FileBrowser.this.dir += "/" + fileNameView.getText().toString();
                    if (chosenDirectoryListener.onChosenDir(FileBrowser.this.dir, true)) {
                        dialog.cancel();
                    }
                }
            }
        });
        final AlertDialog dirsDialog = dialogBuilder.create();
        if (isNewFolderEnabled) {
            fileNameView.addTextChangedListener(new TextWatcher() {
                @Override
                public void afterTextChanged(Editable s) {

                }

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    // TODO Auto-generated method stub

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    setPositiveButton(dirsDialog);
                }

            });
        }
//        dirsDialog.setOnShowListener(new DialogInterface.OnShowListener() {
//            @Override
//            public void onShow(final DialogInterface dialog) {
//
//                Button b = dirsDialog.getButton(AlertDialog.BUTTON_POSITIVE);
//                b.setOnClickListener(new View.OnClickListener() {
//
//                    @Override
//                    public void onClick(View view) {
//                        if (chosenDirectoryListener != null) {
//                            // Call registered listener supplied with the chosen directory
//                            FileBrowser.this.dir += "/" + fileNameView.getText().toString();
//                            if (chosenDirectoryListener.onChosenDir(FileBrowser.this.dir)) {
//                                dirsDialog.dismiss();
//                            } else {
//                                dirsDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
//                                dirsDialog.getButton(AlertDialog.BUTTON_NEUTRAL).setEnabled(true);
//                            }
//                        }
//                    }
//                });
//            }
//        });
//
//        fileNameView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View v, boolean hasFocus) {
//                if (hasFocus) {
//                    dirsDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
//                }
//            }
//        });

        dirsDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
                    // Back button pressed
                    if (FileBrowser.this.dir.equals(sdcardDirectory)) {
                        // The very top level directory, do nothing
                        return false;
                    } else {
                        // Navigate back to an upper directory
                        FileBrowser.this.dir = new File(FileBrowser.this.dir).getParent();
                        updateDirectory();
                    }
                    return true;
                } else {
                    return false;
                }
            }
        });
        dirsDialog.show();
        dirsDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        dirsDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        if (isNewFolderEnabled) {
            setPositiveButton(dirsDialog);
        } else {
            dirsDialog.getButton(AlertDialog.BUTTON_POSITIVE).setVisibility(View.GONE);
            dirsDialog.getButton(AlertDialog.BUTTON_NEUTRAL).setVisibility(View.GONE);
        }

    }

    private void setPositiveButton(AlertDialog dirsDialog) {
        String realString = fileNameView.getText().toString().replace(" ", "");
        if (realString.length() > 0) {
            File file = new File(dir + "/" + realString);
            Log.i("SETBUTTON", dir + "/" + realString);
            if (file.exists()) {
                dirsDialog.getButton(AlertDialog.BUTTON_NEUTRAL).setEnabled(true);
                dirsDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
            } else {
                dirsDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
                dirsDialog.getButton(AlertDialog.BUTTON_NEUTRAL).setEnabled(false);
            }
        } else {
            dirsDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
            dirsDialog.getButton(AlertDialog.BUTTON_NEUTRAL).setEnabled(false);
        }
    }

    private boolean createSubDir(String newDir) {
        File newDirFile = new File(newDir);
        if (!newDirFile.exists()) {
            return newDirFile.mkdir();
        }
        return false;
    }

    private List<String> getDirectories(String dir) {
        List<String> dirs = new ArrayList<String>();
        try {
            File dirFile = new File(dir);
            if (!dirFile.exists() || !dirFile.isDirectory()) {
                return dirs;
            }
            for (File file : dirFile.listFiles()) {
                if (isNewFolderEnabled) {
                    if (file.isDirectory()) {
                        dirs.add(file.getName());
                    }
                } else {
                    dirs.add(file.getName());
                }
            }
        } catch (Exception e) {
        }

        Collections.sort(dirs, new Comparator<String>() {
            public int compare(String o1, String o2) {
                return o1.compareTo(o2);
            }
        });

        return dirs;
    }

    private AlertDialog.Builder createDirectoryChooserDialog(String title, List<String> listItems,
                                                             DialogInterface.OnClickListener onClickListener) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);

        // Create custom view for AlertDialog title containing
        // current directory TextView and possible 'New folder' button.
        // Current directory TextView allows long directory path to be wrapped to multiple lines.
        LinearLayout titleLayout = new LinearLayout(context);
        titleLayout.setOrientation(LinearLayout.VERTICAL);

        titleView = new TextView(context);
        titleView.setLayoutParams(new WindowManager.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        titleView.setTextAppearance(context, android.R.style.TextAppearance_Large);
//        titleView.setTextColor(context.getResources().getColor(android.R.color.holo_blue_dark));
        titleView.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
        titleView.setText(title);

        if (isNewFolderEnabled) {
            fileNameView = new EditText(context);
            fileNameView.setInputType(InputType.TYPE_TEXT_VARIATION_URI);
            fileNameView.setLines(1);
            fileNameView.setLayoutParams(new WindowManager.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
//
//            fileNameView.setFocusable(true);
//            fileNameView.setFocusableInTouchMode(true);
//            fileNameView.setClickable(true);
//
            fileNameView.setTextAppearance(context, android.R.style.TextAppearance_Large);
            fileNameView.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
            fileNameView.setTextColor(Color.BLACK);
//            fileNameView.setHintTextColor(Color.BLACK);
            fileNameView.setBackgroundColor(Color.WHITE);
            fileNameView.setHint(R.string.new_file_name);
//            Editable newFile = fileNameView.getText();

        }
        Button newDirButton = new Button(context);
        newDirButton.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        newDirButton.setText("New folder");
        newDirButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText input = new EditText(context);
                // Show new folder uniqueName input dialog
                new AlertDialog.Builder(context).
                        setTitle("New folder uniqueName").
                        setView(input).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        Editable newDir = input.getText();
                        String newDirName = newDir.toString();
                        // Create new directory
                        if (createSubDir(dir + "/" + newDirName)) {
                            // Navigate into the new directory
                            dir += "/" + newDirName;
                            updateDirectory();
                        } else {
                            Toast.makeText(
                                    context, "Failed to create '" + newDirName +
                                            "' folder", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).setNegativeButton("Cancel", null).show();
            }
        });
        titleLayout.addView(titleView);
        if (!isNewFolderEnabled) {
            newDirButton.setVisibility(View.GONE);
        } else {
            titleLayout.addView(fileNameView);
        }
        titleLayout.addView(newDirButton);
        dialogBuilder.setCustomTitle(titleLayout);
        listAdapter = createListAdapter(listItems);
        dialogBuilder.setSingleChoiceItems(listAdapter, -1, onClickListener);
        dialogBuilder.setCancelable(false);
        return dialogBuilder;
    }

    private void updateDirectory() {
        subdirs.clear();
        subdirs.addAll(getDirectories(dir));
        titleView.setText(dir);
        listAdapter.notifyDataSetChanged();
    }

    private ArrayAdapter<String> createListAdapter(List<String> items) {
        return new ArrayAdapter<String>(context,
                android.R.layout.select_dialog_item, android.R.id.text1, items) {
            @Override
            public View getView(int position, View convertView,
                                ViewGroup parent) {
                View v = super.getView(position, convertView, parent);
                if (v instanceof TextView) {
                    TextView tv = (TextView) v;
                    tv.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
                    tv.setEllipsize(null);
                }
                return v;
            }
        };
    }

    public interface ChosenDirectoryListener {
        public boolean onChosenDir(String chosenDir, boolean overWrite);
    }

    public interface CancelListener {
        public void onCancel(String chosenDir);
    }
}