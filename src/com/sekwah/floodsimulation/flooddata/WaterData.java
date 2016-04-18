package com.sekwah.floodsimulation.flooddata;

/**
 * Created by on 29/03/2016.
 *
 * @author sekwah41
 */
public class WaterData implements Comparable<WaterData> {

    /**
     * From 0 to 100
     *
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
     *
     * Also store some data as metadata for when the blocks come back.
     */
    public float level = 0;

    public FloodPos pos;

    public boolean hasChanged = false;

    /**
     * If it is a new block it will set the current block to it.
     */
    public boolean newBlock = true;

    public int inactiveTicks = 0;

    public WaterData(FloodPos pos, float fillAmount){
        this.level = fillAmount;
        this.pos = pos;
    }

    @Override
    public int compareTo(WaterData waterData) {
        return (int) (this.level - waterData.level);
    }

    public void change(float amount){
        if(amount == 0){
            return;
        }
        this.level += amount;
        hasChanged = true;
        if(this.level > 50 && amount > 0.5){
            inactiveTicks = 0;
        }
    }

    public void change(){
        hasChanged = true;
        inactiveTicks = 0;
    }
}
