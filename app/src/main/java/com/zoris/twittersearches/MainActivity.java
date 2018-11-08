// MainActivity.java
// Manages your favorite Twitter searches for easy
// access and display in the device's web browser
package com.zoris.twittersearches;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
   // name of SharedPreferences XML file that stores the saved searches
   private static final String SEARCHES = "searches";

   private EditText queryEditText; // where user enters a query
   private EditText tagEditText; // where user enters a query's tag
   private FloatingActionButton saveFloatingActionButton; // save search
   private SharedPreferences savedSearches; // user's favorite searches
   private List<String> tags; // list of tags for saved searches
   private HashMap<String, String> timeMap;
   private SearchesAdapter adapter; // for binding data to RecyclerView

   public boolean onCreateOptionsMenu(Menu menu) {
      // get the device's current orientation
      int orientation = getResources().getConfiguration().orientation;

      // display the app's menu only in portrait orientation
      if (orientation == Configuration.ORIENTATION_PORTRAIT) {
         // inflate the menu
         getMenuInflater().inflate(R.menu.fragment_exit_menu, menu);
         return true;
      }
      else
         return false;
   }

   public boolean onOptionsItemSelected(MenuItem item) {
      switch (item.getItemId()) {

         case R.id.action_exit:
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(1);
            return true;
      }

      return super.onOptionsItemSelected(item);
   }
   // configures the GUI and registers event listeners
   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_main);
      Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
      setSupportActionBar(toolbar);

      timeMap = new HashMap<>();

      // get references to the EditTexts and add TextWatchers to them
      queryEditText = ((TextInputLayout) findViewById(
         R.id.queryTextInputLayout)).getEditText();
      queryEditText.addTextChangedListener(textWatcher);
      tagEditText = ((TextInputLayout) findViewById(
         R.id.tagTextInputLayout)).getEditText();
      tagEditText.addTextChangedListener(textWatcher);

      // get the SharedPreferences containing the user's saved searches
      savedSearches = getSharedPreferences(SEARCHES, MODE_PRIVATE);

      // store the saved tags in an ArrayList then sort them
      tags = new ArrayList<>(savedSearches.getAll().keySet());
      Collections.sort(tags, String.CASE_INSENSITIVE_ORDER);


      //pulling time out of hashSet
      for (Map.Entry<String, ?> entry : savedSearches.getAll().entrySet()) {
          if (entry.getValue() instanceof HashSet) {
              Set<String> set = (HashSet)entry.getValue();
              set.get
              timeMap.put(set)
          }
      }


      // get reference to the RecyclerView to configure it
      RecyclerView recyclerView =
         (RecyclerView) findViewById(R.id.recyclerView);

      // use a LinearLayoutManager to display items in a vertical list
      recyclerView.setLayoutManager(new LinearLayoutManager(this));

      // create RecyclerView.Adapter to bind tags to the RecyclerView
      adapter = new SearchesAdapter(
              tags, timeMap, itemClickListener, itemLongClickListener);
      recyclerView.setAdapter(adapter);

      // specify a custom ItemDecorator to draw lines between list items
      recyclerView.addItemDecoration(new ItemDivider(this));

      // register listener to save a new or edited search
      saveFloatingActionButton =
         (FloatingActionButton) findViewById(R.id.fab);
      saveFloatingActionButton.setOnClickListener(saveButtonListener);
      updateSaveFAB(); // hides button because EditTexts initially empty
   }

   // hide/show saveFloatingActionButton based on EditTexts' contents
   private final TextWatcher textWatcher = new TextWatcher() {
      @Override
      public void beforeTextChanged(CharSequence s, int start, int count,
         int after) { }

      // hide/show the saveFloatingActionButton after user changes input
      @Override
      public void onTextChanged(CharSequence s, int start, int before,
         int count) {
         updateSaveFAB();
      }

      @Override
      public void afterTextChanged(Editable s) { }
   };

   // shows or hides the saveFloatingActionButton
   private void updateSaveFAB() {
      // check if there is input in both EditTexts
      if (queryEditText.getText().toString().isEmpty() ||
         tagEditText.getText().toString().isEmpty())
         saveFloatingActionButton.hide();
      else
         saveFloatingActionButton.show();
   }

   // saveButtonListener save a tag-query pair into SharedPreferences
   private final OnClickListener saveButtonListener =
      new OnClickListener() {
         // add/update search if neither query nor tag is empty
         @Override
         public void onClick(View view) {
            String query = queryEditText.getText().toString();
            String tag = tagEditText.getText().toString();


            if (!query.isEmpty() && !tag.isEmpty()) {
               // hide the virtual keyboard
               ((InputMethodManager) getSystemService(
                  Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(
                     view.getWindowToken(), 0);

               addTaggedSearch(tag, query); // add/update the search
               queryEditText.setText(""); // clear queryEditText
               tagEditText.setText(""); // clear tagEditText
               queryEditText.requestFocus(); // queryEditText gets focus
            }
         }
      };

   // add new search to file, then refresh all buttons
   private void addTaggedSearch(String tag, String query) {
      // TODO: Compare to previous timeString, save if new
      SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");


      String timeString = sdf.format(Calendar.getInstance().getTime());

      HashSet<String> set = new HashSet<String>();
      set.add(query);
      set.add(timeString);

      // get a SharedPreferences.Editor to store new tag/query pair
      SharedPreferences.Editor preferencesEditor = savedSearches.edit();
      preferencesEditor.putStringSet(tag, set); // store current search
      preferencesEditor.apply(); // store the updated preferences

      // if tag is new, add to and sort tags, then display updated list
      if (!tags.contains(tag)) {
         tags.add(tag); // add new tag
         timeMap.put(tag, timeString);
         Collections.sort(tags, String.CASE_INSENSITIVE_ORDER);
         adapter.notifyDataSetChanged(); // update tags in RecyclerView
      }
   }

   // itemClickListener launches web browser to display search results
   private final OnClickListener itemClickListener =
      new OnClickListener() {
         @Override
         public void onClick(View view) {
            // get query string and create a URL representing the search
            String tag = ((TextView) view).getText().toString();
            String urlString = getString(R.string.search_URL) +
               Uri.encode(savedSearches.getString(tag, ""), "UTF-8");

            // create an Intent to launch a web browser
            Intent webIntent = new Intent(Intent.ACTION_VIEW,
               Uri.parse(urlString));

            startActivity(webIntent); // show results in web browser
         }
      };

   // itemLongClickListener displays a dialog allowing the user to share
   // edit or delete a saved search
   private final OnLongClickListener itemLongClickListener =
      new OnLongClickListener() {
         @Override
         public boolean onLongClick(View view) {
            // get the tag that the user long touched
            final String tag = ((TextView) view).getText().toString();

            // create a new AlertDialog
            AlertDialog.Builder builder =
               new AlertDialog.Builder(MainActivity.this);

            // set the AlertDialog's title
            builder.setTitle(
               getString(R.string.share_edit_delete_title, tag));

            // set list of items to display and create event handler
            builder.setItems(R.array.dialog_items,
               new DialogInterface.OnClickListener() {
                  @Override
                  public void onClick(DialogInterface dialog, int which) {
                     switch (which) {
                        case 0: // share
                           shareSearch(tag);
                           break;
                        case 1: // edit
                           // set EditTexts to match chosen tag and query
                           tagEditText.setText(tag);
                           queryEditText.setText(
                              savedSearches.getString(tag, ""));
                           break;
                        case 2: // delete
                           deleteSearch(tag);
                           break;
                     }
                  }
               }
            );

            // set the AlertDialog's negative Button
            builder.setNegativeButton(getString(R.string.cancel), null);

            builder.create().show(); // display the AlertDialog
            return true;
         }
      };

   // allow user to choose an app for sharing URL of a saved search
   private void shareSearch(String tag) {
      // create the URL representing the search
      String urlString = getString(R.string.search_URL) +
         Uri.encode(savedSearches.getString(tag, ""), "UTF-8");

      // create Intent to share urlString
      Intent shareIntent = new Intent();
      shareIntent.setAction(Intent.ACTION_SEND);
      shareIntent.putExtra(Intent.EXTRA_SUBJECT,
         getString(R.string.share_subject));
      shareIntent.putExtra(Intent.EXTRA_TEXT,
         getString(R.string.share_message, urlString));
      shareIntent.setType("text/plain");

      // display apps that can share plain text
      startActivity(Intent.createChooser(shareIntent,
         getString(R.string.share_search)));
   }

   // deletes a search after the user confirms the delete operation
   private void deleteSearch(final String tag) {
      // create a new AlertDialog and set its message
      AlertDialog.Builder confirmBuilder = new AlertDialog.Builder(this);
      confirmBuilder.setMessage(getString(R.string.confirm_message, tag));

      // configure the negative (CANCEL) Button
      confirmBuilder.setNegativeButton(getString(R.string.cancel), null);

      // configure the positive (DELETE) Button
      confirmBuilder.setPositiveButton(getString(R.string.delete),
         new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
               tags.remove(tag);
               timeMap.remove(tag);
               // get SharedPreferences.Editor to remove saved search
               SharedPreferences.Editor preferencesEditor =
                  savedSearches.edit();
               preferencesEditor.remove(tag); // remove search
               preferencesEditor.apply(); // save the changes

               // rebind tags to RecyclerView to show updated list
               adapter.notifyDataSetChanged();
            }
         }
      );

      confirmBuilder.create().show(); // display AlertDialog
   }
}

/**************************************************************************
 * (C) Copyright 1992-2016 by deitel & Associates, Inc. and               *
 * Pearson Education, Inc. All Rights Reserved.                           *
 *                                                                        *
 * DISCLAIMER: The authors and publisher of this book have used their     *
 * best efforts in preparing the book. These efforts include the          *
 * development, research, and testing of the theories and programs        *
 * to determine their effectiveness. The authors and publisher make       *
 * no warranty of any kind, expressed or implied, with regard to these    *
 * programs or to the documentation contained in these books. The authors *
 * and publisher shall not be liable in any event for incidental or       *
 * consequential damages in connection with, or arising out of, the       *
 * furnishing, performance, or use of these programs.                     *
 **************************************************************************/
