package org.freevoice.mapcoloring.model;

/**
 * Interface which allows users of the map coloring to track progress.
 *
 * Created by lreeder on 3/16/14.
 */
public interface ColoringProgressListener
{
   public void update(int percentDone, String message);

   /**
    * Update progress with percentage done.
    *
    * @param percentDone Value between 0 and 100 (inclusive)
    */
   public void update(int percentDone);
}
