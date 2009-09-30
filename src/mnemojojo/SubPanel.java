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
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;

import gr.fire.core.FireScreen;
import gr.fire.core.Component;
import gr.fire.core.Container;

import gr.fire.core.BoxLayout;
import gr.fire.core.Container;
import gr.fire.core.GridLayout;
import gr.fire.ui.InputComponent;
import gr.fire.ui.TextComponent;

class SubPanel
    extends gr.fire.core.Panel
    implements CommandListener,
	       gr.fire.core.CommandListener
{
    protected FireScreen screen;
    protected gr.fire.core.CommandListener listener;
    protected Command cmdDone;

    protected Command cmdButton;

    protected static Font sectionFont;
    protected static Font titleFont;
    protected static Font labelFont;
    protected static Font textFont;

    protected static int sectionFontHeight;
    protected static int titleFontHeight;
    protected static int labelFontHeight;
    protected static int textFontHeight;
    protected static int buttonHeight;

    protected int screenWidth;
    protected final int controlGap = 10;
    protected final int edgeGap = 10;

    protected int buttonBgColor;
    protected int buttonFgColor;

    protected Command cmdLeft;
    protected Command cmdRight;

    public SubPanel(String title, FireScreen s, gr.fire.core.CommandListener li,
		    Command cmd)
    {
	super(null, Panel.VERTICAL_SCROLLBAR, true);
	
	screen = s;
	listener = li;
	cmdDone = cmd;
	setLabel(title);

	screenWidth = screen.getWidth() - edgeGap;

	// setup fonts
	sectionFont = Font.getFont(Font.FACE_SYSTEM,
				   Font.STYLE_BOLD,
				   Font.SIZE_LARGE);
	sectionFontHeight = sectionFont.getHeight();

	titleFont = Font.getFont(Font.FACE_SYSTEM,
				 Font.STYLE_BOLD,
				 Font.SIZE_SMALL);
	titleFontHeight = titleFont.getHeight();

	textFont = Font.getFont(Font.FACE_SYSTEM,
				Font.STYLE_PLAIN,
				Font.SIZE_SMALL);
	textFontHeight = textFont.getHeight();

	labelFont = titleFont;
	labelFontHeight = titleFontHeight;

	buttonHeight = labelFontHeight * 2;
	buttonBgColor = FireScreen.getTheme().getIntProperty("button.bg.color");
	buttonFgColor = FireScreen.getTheme().getIntProperty("button.fg.color");

	cmdButton = new Command("invisible", Command.OK, 1);
    }

    protected void exitPanel(Command cmd)
    {
	listener.commandAction(cmd, (Component)this);
    }

    protected void exitPanel()
    {
	exitPanel(cmdDone);
    }

    protected Container titleRow(String title, int extraGap)
    {
	Container row = new Container(new BoxLayout(BoxLayout.Y_AXIS));

	int titleWidth = screenWidth / 2;
	
	TextComponent titleCmp = new TextComponent(title, screenWidth);
	titleCmp.setFont(sectionFont);

	int valign = FireScreen.TOP;
	if (extraGap > 0) {
	    valign = FireScreen.BOTTOM;
	}

	titleCmp.setLayout(valign | FireScreen.CENTER);
	titleCmp.validate();

	row.add(titleCmp);
	row.setPrefSize(screenWidth, sectionFontHeight + extraGap);

	return row;
    }

    protected Container fieldRow(String title, String text)
    {
	Container row = new Container(new BoxLayout(BoxLayout.X_AXIS));

	int titleWidth = (int)(screenWidth / 2.3);
	
	TextComponent titleCmp = new TextComponent(title + ":", titleWidth);
	titleCmp.setFont(titleFont);
	titleCmp.setLayout(FireScreen.TOP | FireScreen.LEFT);
	titleCmp.validate();

	TextComponent textCmp = new TextComponent(text, titleWidth);
	textCmp.setFont(textFont);
	textCmp.setLayout(FireScreen.TOP | FireScreen.LEFT);
	textCmp.validate();

	row.add(titleCmp);
	row.add(textCmp);
	row.setPrefSize(screenWidth, titleFontHeight);

	return row;
    }

    protected InputComponent checkboxRow(String text, Container cnt)
    {
	Container row = new Container(new BoxLayout(BoxLayout.X_AXIS));

	InputComponent checkbox = new InputComponent(InputComponent.CHECKBOX);
	checkbox.setValue(text);
	checkbox.setCommandListener(this);
	checkbox.setCommand(cmdButton);
	checkbox.setLayout(FireScreen.CENTER | FireScreen.VCENTER);
	checkbox.setBackgroundColor(buttonBgColor);
	checkbox.setForegroundColor(buttonFgColor);
	checkbox.setPrefSize(InputComponent.RADIO_WIDTH,
			     InputComponent.RADIO_HEIGHT);
	checkbox.setLeftSoftKeyCommand(cmdLeft);
	checkbox.setRightSoftKeyCommand(cmdRight);
	checkbox.validate();

	TextComponent title = new TextComponent("  " + text,
				screenWidth - InputComponent.RADIO_WIDTH);
	title.setFont(labelFont);
	title.setLayout(FireScreen.LEFT | FireScreen.VCENTER);
	title.validate();

	row.add(checkbox);
	row.add(title);
	row.setPrefSize(screenWidth, labelFontHeight + controlGap);
	row.validate();

	cnt.add(row);

	return checkbox;
    }

    protected Container buttonRow(String text)
    {
	Container row = new Container(new BoxLayout(BoxLayout.X_AXIS));

	InputComponent button = new InputComponent(InputComponent.BUTTON);
	button.setValue(text);
	button.setCommandListener(this);
	button.setCommand(cmdButton);
	button.setFont(labelFont);
	button.setLayout(FireScreen.CENTER | FireScreen.VCENTER);
	button.setBackgroundColor(buttonBgColor);
	button.setForegroundColor(buttonFgColor);
	button.setPrefSize(screenWidth, buttonHeight);
	button.setLeftSoftKeyCommand(cmdLeft);
	button.setRightSoftKeyCommand(cmdRight);
	button.validate();

	row.add(button);
	row.setPrefSize(screenWidth, buttonHeight + controlGap);
	row.validate();

	return row;
    }

    protected InputComponent radioRow(String text, int width, Container cnt)
    {
	Container row = new Container(new BoxLayout(BoxLayout.X_AXIS));

	int inputHeight = Math.max(labelFontHeight,
				   InputComponent.RADIO_HEIGHT);

	InputComponent radio = new InputComponent(InputComponent.RADIO);
	radio.setValue(text);
	radio.setCommandListener(this);
	radio.setCommand(cmdButton);
	radio.setLayout(FireScreen.CENTER | FireScreen.VCENTER);
	radio.setBackgroundColor(buttonBgColor);
	radio.setForegroundColor(buttonFgColor);
	radio.setPrefSize(InputComponent.RADIO_WIDTH,
			  InputComponent.RADIO_HEIGHT);
	radio.setLeftSoftKeyCommand(cmdLeft);
	radio.setRightSoftKeyCommand(cmdRight);
	radio.validate();

	TextComponent title = new TextComponent("  " + text,
				width - InputComponent.RADIO_WIDTH);
	title.setFont(labelFont);
	title.setLayout(FireScreen.LEFT | FireScreen.VCENTER);
	title.validate();

	row.add(radio);
	row.add(title);
	row.setLayout(FireScreen.LEFT | FireScreen.VCENTER);
	row.setPrefSize(width, inputHeight);

	cnt.add(row);

	return radio;
    }

    public void commandAction(javax.microedition.lcdui.Command cmd,
			      Displayable d)
    {
    }

    public void commandAction(javax.microedition.lcdui.Command cmd,
			      Component c)
    {
    }
}

