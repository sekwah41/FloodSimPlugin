package com.sekwah.floodsimulation.flooddata;

/**
 * Created by on 29/03/2016.
 *
 * Where the flood water will be added. if the previous ones are full then they will move up if possible.
 *
 * Also expand out to the sides if there are other full blocks there.
 *
 * @author sekwah41
 */
public class FloodSource {

    /**
     * Is a perma source if the block is a lapis, could convert to water but it makes it easier to realise whats going on.
     */
    public boolean permaSource = false;

    public FloodPos pos;

    public FloodSource(FloodPos pos, boolean permaSource){
        this.pos = pos;
        this.permaSource = permaSource;
    }

}
