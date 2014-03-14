/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.missouri.teva;

import edu.mit.cci.text.windowing.WindowStrategy;
import edu.mit.cci.text.windowing.Windowable;
import edu.mit.cci.util.U;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 *
 * @author Ryan
 */
public class LookBehindWindowStrategy implements WindowStrategy<Windowable> {
    private Date[][] windows;
    private List<? extends Windowable> data;
    private int priortokens;
    private boolean addFirst = false;

    public LookBehindWindowStrategy(int priortokens, Date[]... windows) {
        this.windows = windows;
        this.priortokens = priortokens;
    }

    public LookBehindWindowStrategy(int priortokens, boolean addFirst, Date[]... windows) {
        this.windows = windows;
        this.priortokens = priortokens;
        this.addFirst = addFirst;
    }

    @Override
    public int getNumberWindows() {
        return windows.length;
    }

    @Override
    public List<Windowable> getWindow(int i) {
        if(data == null) {
            TEvA.log("RETRIEVING WINDOW WITH NULL DATA!");
        }
        
        int end = U.binarySearch(data, windows[i][1], new Comparator<Date>() {
            @Override
            public int compare(Date o1, Date o2) {
                return o1.compareTo(o2);
            }
        }, new U.Adapter<Windowable, Date>() {
            @Override
            public Date adapt(Windowable t) {
                return t.getStart();
            }
        });
        if (end < 0) {
            end = -1 * (end + 2);
        }
        if (end < 0 || data.get(end).getStart().compareTo(windows[i][0]) < 0) {
            return Collections.emptyList();
        } else {
            int tokenCount = 0;
            int start = end;
            while (start > 0 && tokenCount < priortokens) {
                tokenCount += (data.get(start--).getTokens().size());
            }
            if (start == 0 || !addFirst) {
                return (List<Windowable>) data.subList(start, end);
            } else {
                return (List<Windowable>) data.subList(0, end);
            }
        }
    }

    @Override
    public Date[][] getWindowBoundaries() {
        return windows;
    }

    @Override
    public void setData(List<? extends Windowable> list) {
        this.data = list;
        TEvA.log("SETTING DATA: "+list);
        if(this.data == null) {
            (new Throwable()).printStackTrace();
        }
    }
    
}
