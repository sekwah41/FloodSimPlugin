package com.sekwah.floodsimulation.flooddata;

/**
 * Created by on 29/03/2016.
 *
 * @author sekwah41
 */
public class WaterData {

    /**
     * Rises to 100. If it is close to 100 or overflowing change to source block and stop tracking.
     * though make the tolerance really strict and the block spread strict to make sure there is a level plane at the moment in time.
     * Also build a large glass border or detect the edge of the flood region. Also dont make the source blocks keep pouring and pouring.
     * Do like mc and do a .blockUpdate to make sure the nearby blocks are checked when stuff changes.
     *
     * Also only add some water every now and then or once everything has settled down.
     * (have it to be able to change the flood interval).
     *
     * Also detect all connected water and calculate the waters sizes. Then get the top of each pool. May be useless though.
     *
     * New idea. Check if the block can see the sky(stops inside pools or stuff like toilets flooding houses xD)
     */
    public float fillAmout = 0;

}
