/*
 * Copyright (c) 2009 Timothy Bourke
 * All rights reserved.
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the "BSD License" which is distributed with the
 * software in the file LICENSE.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the BSD
 * License for more details.
 */

package mnemojojo;

import java.lang.*;
import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;
import javax.microedition.lcdui.Font;

import gr.fire.core.FireScreen;

public class Configuration
{
    public String cardPath;
    public long cards_mtime;
    public int leftSoftKey;
    public int rightSoftKey;

    public boolean showButtons;
    public int gradeKey[];
    public int skipKey;
    public int statKey;
    public int fontSize;

    public int defaultGradeKey[] = {
	    FireScreen.KEY_NUM0,
	    FireScreen.KEY_NUM1,
	    FireScreen.KEY_NUM2,
	    FireScreen.KEY_NUM3,
	    FireScreen.KEY_NUM4,
	    FireScreen.KEY_NUM5
	};

    public Configuration() {
	gradeKey = new int[defaultGradeKey.length];
	load();
    }

    private String readRecord(String name) {
	String r;

	try {
	    RecordStore rs = RecordStore.openRecordStore(name, false);
	    r = new String(rs.getRecord(1));
	    System.out.println("config: " + name + "=" + r); // XXX
	    rs.closeRecordStore();
	} catch (RecordStoreException e) {
	    r = null;
	}

	return r;
    }

    private void writeRecord(String name, String value) {
	byte[] data = value.getBytes();

	try {
	    RecordStore rs = RecordStore.openRecordStore(name, true);
	    if (rs.getNumRecords() == 0) {
		rs.addRecord(data, 0, data.length);
	    } else {
		rs.setRecord(1, data, 0, data.length);
	    }
	    rs.closeRecordStore();
	} catch (RecordStoreException e) { }
    }

    public void load() {
	cardPath = readRecord("cardpath");
	if (cardPath == null) {
	    cardPath = new String("");
	}

	String v = readRecord("cards_mtime");
	if (v == null) {
	    cards_mtime = 0;
	} else {
	    cards_mtime = Long.parseLong(v);
	}

	v = readRecord("left_soft_key");
	if (v == null) {
	    leftSoftKey = 0;
	} else {
	    leftSoftKey = Integer.parseInt(v);
	}

	v = readRecord("right_soft_key");
	if (v == null) {
	    rightSoftKey = 0;
	} else {
	    rightSoftKey = Integer.parseInt(v);
	}

	v = readRecord("show_buttons");
	if (v == null) {
	    showButtons = true;
	} else {
	    showButtons = v.equals("true");
	}

	for (int i=0; i < gradeKey.length; ++i) {
	    v = readRecord("grade_" + Integer.toString(i));
	    if (v == null) {
		gradeKey[i] = defaultGradeKey[i];
	    } else {
		gradeKey[i] = Integer.parseInt(v);
	    }
	    System.out.println("grade_" + Integer.toString(i) + "=" + gradeKey[i]);//XXX
	}

	v = readRecord("skip_key");
	if (v == null) {
	    skipKey = FireScreen.KEY_STAR;
	} else {
	    skipKey = Integer.parseInt(v);
	}

	v = readRecord("stat_key");
	if (v == null) {
	    statKey = FireScreen.KEY_POUND;
	} else {
	    statKey = Integer.parseInt(v);
	}

	v = readRecord("font_size");
	if (v == null) {
	    fontSize = Font.SIZE_SMALL;
	} else {
	    fontSize = Integer.parseInt(v);
	}
    }

    public void save()
    {
	writeRecord("cardpath", cardPath);
	writeRecord("cards_mtime", Long.toString(cards_mtime));

	writeRecord("left_soft_key", Integer.toString(leftSoftKey));
	writeRecord("right_soft_key", Integer.toString(rightSoftKey));
	
	writeRecord("show_buttons", showButtons?"true":"false");
	for (int i=0; i < gradeKey.length; ++i) {
	    writeRecord("grade_" + Integer.toString(i),
			Integer.toString(gradeKey[i]));
	}
	writeRecord("skip_key", Integer.toString(skipKey));
	writeRecord("stat_key", Integer.toString(statKey));
	writeRecord("font_size", Integer.toString(fontSize));
    }
}

