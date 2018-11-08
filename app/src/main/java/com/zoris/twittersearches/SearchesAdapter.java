// SearchesAdapter.java
// Subclass of RecyclerView.Adapter for binding data to RecyclerView items
package com.zoris.twittersearches;

import android.app.DownloadManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Calendar;
import java.util.List;

public class SearchesAdapter
   extends RecyclerView.Adapter<SearchesAdapter.ViewHolder> {

   // listeners from MainActivity that are registered for each list item
   private final View.OnClickListener clickListener;
   private final View.OnLongClickListener longClickListener;

   // List<String> used to obtain RecyclerView items' data
   private final List<String> queries; // search tags

   // constructor
   public SearchesAdapter(List<String> queries,
      View.OnClickListener clickListener,
      View.OnLongClickListener longClickListener) {
      this.queries = queries;
      this.clickListener = clickListener;
      this.longClickListener = longClickListener;
   }

   // nested subclass of RecyclerView.ViewHolder used to implement
   // the view-holder pattern in the context of a RecyclerView--the logic
   // of recycling views that have scrolled offscreen is handled for you
   public static class ViewHolder extends RecyclerView.ViewHolder {
       public final TextView textViewTag;
       public final TextView textViewTime;

      // configures a RecyclerView item's ViewHolder
      public ViewHolder(View itemView,
         View.OnClickListener clickListener,
         View.OnLongClickListener longClickListener) {
         super(itemView);
          textViewTag = (TextView) itemView.findViewById(R.id.textViewTag);
          textViewTime = (TextView) itemView.findViewById(R.id.textViewTime);

         // attach listeners to itemView
         itemView.setOnClickListener(clickListener);
         itemView.setOnLongClickListener(longClickListener);
      }
   }

   // sets up new list item and its ViewHolder
   @Override
   public ViewHolder onCreateViewHolder(ViewGroup parent,
      int viewType) {
      // inflate the list_item layout
      View view = LayoutInflater.from(parent.getContext()).inflate(
         R.layout.list_item, parent, false);

      // create a ViewHolder for current item
      return (new ViewHolder(view, clickListener, longClickListener));
   }

   // sets the text of the list item to display the search tag
   @Override
   public void onBindViewHolder(ViewHolder holder, int position) {
       holder.textViewTag.setText(queries.get(position));
       holder.textViewTime.setText(Calendar.getInstance().toString());
   }

   // returns the number of items that adapter binds
   @Override
   public int getItemCount() {
      return queries.size();
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
