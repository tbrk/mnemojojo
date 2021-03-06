/*
 * Copyright (C) 2010 Timothy Bourke
 * 
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation; either version 2 of the License, or (at your option)
 * any later version.
 * 
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License
 * for more details.
 * 
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc., 59
 * Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */

package mnemojojo;

import java.io.IOException;

import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Image;

import gr.fire.core.FireScreen;
import gr.fire.core.Component;
import gr.fire.core.BoxLayout;
import gr.fire.core.Container;
import gr.fire.core.GridLayout;
import gr.fire.ui.InputComponent;
import gr.fire.ui.ImageComponent;
import gr.fire.ui.TextComponent;
import gr.fire.ui.TextArea;

public class AboutPanel
    extends SubPanel
    implements CommandListener,
               gr.fire.core.CommandListener
{
    protected Command cmdLeftRightDone;
    protected Command cmdKeysDone;
    protected Command cmdChangeDir;

    protected InputComponent cardpathBox;
    protected InputComponent smallRadio;
    protected InputComponent mediumRadio;
    protected InputComponent largeRadio;
    protected InputComponent touchscreenCheck;
    protected InputComponent centerTextCheck;
    protected InputComponent autoPlayCheck;
    protected InputComponent darkModeCheck;
    protected InputComponent cardstoloadNum;
    protected boolean cardstoloadSet = false;

    protected final static String authorText = "Author";
    protected final static String mnemosyneText = "Mnemosyne";
    protected final static String sm2Text = "SM2 Algorithm";
    protected final static String fireguiText = "Fire UI";

    protected final static String configTitleText = "Configuration";
    protected final static String changeCardsText= "Change card directory";
    protected final static String touchscreenText= "Show touchscreen buttons";
    protected final static String leftrightText="Configure command keys";
    protected final static String gradingkeysText= "Configure grading keys";
    protected final static String cardfontText= "Card font size:";
    protected final static String smallText= "small";
    protected final static String mediumText= "medium";
    protected final static String largeText= "large";
    protected final static String cardsToLoadText= "Cards to load ahead:";
    protected final static String centerTextText= "Center card text";
    protected final static String autoPlayText= "Play sounds automatically";
    protected final static String darkModeText= "Dark mode";
    protected final static String nocardpathText
                                = "(no card directory currently set)";

    public boolean dirty = false;
    public int fontSize = Font.SIZE_SMALL;
    public boolean touchScreen = true;
    public boolean centerText = false;
    public boolean autoPlay = true;
    public boolean darkMode = false;
    public int keys[];
    public String cardpath;
    public int cardsToLoad;

    private Container aboutCnt;

    public AboutPanel(FireScreen screen, String versionInfo,
                      gr.fire.core.CommandListener li,
                      Command cmdLeft, Command cmdRight,
                      Configuration config)
    {
        super(versionInfo, screen, li, null, config);

        this.cmdLeft = cmdLeft;
        this.cmdRight = cmdRight;
        setCommandListener(this);       
        setLeftSoftKeyCommand(cmdLeft);
        setRightSoftKeyCommand(cmdRight);

        keys = new int[MapKeysPanel.keyQuery.length];

        cmdLeftRightDone = new Command("invisible", Command.OK, 1);
        cmdKeysDone = new Command("invisible", Command.OK, 1);
        cmdChangeDir = new Command("invisible", Command.OK, 1);

        buildPanel();
    }

    private void buildPanel()
    {
        aboutCnt = new Container(new BoxLayout(BoxLayout.Y_AXIS));

        // title image
        try {
            Image img;
            if (FireMIDlet.blackberry) {
            	img = Image.createImage("/res/mnemosyne.png");
            } else {
            	img = Image.createImage("/mnemosyne.png");
            }
            ImageComponent imgCmp = new ImageComponent(img, "");
            imgCmp.setLayout(FireScreen.VCENTER | FireScreen.CENTER);
            imgCmp.setPrefSize(img.getWidth(), img.getHeight() + 15);
            imgCmp.validate();
            aboutCnt.add(imgCmp);
        } catch (IOException e) { }

        // credits
        aboutCnt.add(fieldRow(authorText, "Timothy Bourke"));
        aboutCnt.add(fieldRow(mnemosyneText, "Peter Bienstman"));
        aboutCnt.add(fieldRow(sm2Text, "Piotr Wozniak"));
        aboutCnt.add(fieldRow(fireguiText, "Pashalis Padeleris"));

        aboutCnt.add(titleRow(configTitleText, labelFontHeight * 2));

        aboutCnt.add(buttonRow(changeCardsText));

        cardpathBox = new InputComponent(InputComponent.TEXT);
        cardpathBox.setFont(textFont);
        cardpathBox.setBorder(false);
        cardpathBox.setEnabled(false);
        cardpathBox.setLayout(FireScreen.TOP | FireScreen.CENTER);
        cardpathBox.validate();

        Container cardpathRow = new Container(new BoxLayout(BoxLayout.Y_AXIS));
        cardpathRow.add(cardpathBox);
        cardpathRow.setPrefSize(screenWidth, (int)(textFontHeight * 1.5));
        aboutCnt.add(cardpathRow);

        touchscreenCheck = checkboxRow(touchscreenText, aboutCnt);
        if (!FireMIDlet.blackberry) {
            aboutCnt.add(buttonRow(leftrightText));
        }
        aboutCnt.add(buttonRow(gradingkeysText));

        aboutCnt.add(fontsizeRow());
        centerTextCheck = checkboxRow(centerTextText, aboutCnt);
        autoPlayCheck = checkboxRow(autoPlayText, aboutCnt);
        darkModeCheck = checkboxRow(darkModeText, aboutCnt);

        cardstoloadNum = numberRow(cardsToLoadText, aboutCnt);

        set(aboutCnt);
        repaintControls();
    }

    private Container fontsizeRow()
    {
        Container row = new Container(new GridLayout(3, 2));

        int titleWidth = labelFont.stringWidth(cardfontText) + 5;
        TextComponent title = new TextComponent(cardfontText, titleWidth);
        title.setFont(labelFont);
        title.setLayout(FireScreen.LEFT | FireScreen.TOP);
        title.validate();

        TextComponent blank1 = new TextComponent("");
        TextComponent blank2 = new TextComponent("");

        int radioWidth = screenWidth - titleWidth;
        row.add(title);
        smallRadio = radioRow(smallText, radioWidth, row);
        row.add(blank1);
        mediumRadio = radioRow(mediumText, radioWidth, row);
        row.add(blank2);
        largeRadio = radioRow(largeText, radioWidth, row);

        row.setPrefSize(screenWidth, largeRadio.getHeight() * 3 + controlGap);

        return row;
    }

    public void repaintControls()
    {
        touchscreenCheck.setChecked(touchScreen);
        touchscreenCheck.repaint();
        centerTextCheck.setChecked(centerText);
        centerTextCheck.repaint();
        autoPlayCheck.setChecked(autoPlay);
        autoPlayCheck.repaint();
        darkModeCheck.setChecked(darkMode);
        darkModeCheck.repaint();

        if (!cardstoloadSet) {
            cardstoloadNum.setValue(Integer.toString(cardsToLoad));
        }

        smallRadio.setChecked(fontSize == Font.SIZE_SMALL);
        smallRadio.repaint();
        mediumRadio.setChecked(fontSize == Font.SIZE_MEDIUM);
        mediumRadio.repaint();
        largeRadio.setChecked(fontSize == Font.SIZE_LARGE);
        largeRadio.repaint();

        if (cardpath == null) {
            cardpathBox.setValue(nocardpathText);
        } else {
            cardpathBox.setValue(cardpath);
        }
        cardpathBox.repaint();
    }

    public void commandAction(javax.microedition.lcdui.Command cmd, Displayable d)
    {
    }

    public void commandAction(javax.microedition.lcdui.Command cmd, Component c)
    {
        if (cmdButton.equals(cmd)) {
            InputComponent input = (InputComponent)c;
            String val = input.getValue();

            if (changeCardsText.equals(val)) {
                CardDirPanel cdp =
                    new CardDirPanel(screen, this, cmdChangeDir, config);
                cdp.cardpath = cardpath;
                screen.setCurrent(cdp);
                cdp.makeList(false);

            } else if (touchscreenText.equals(val)) {
                touchScreen = !input.isChecked();
                dirty = true;

            } else if (centerTextText.equals(val)) {
                centerText = !input.isChecked();
                dirty = true;

            } else if (autoPlayText.equals(val)) {
                autoPlay = !input.isChecked();
                dirty = true;

            } else if (darkModeText.equals(val)) {
                darkMode = !input.isChecked();
                dirty = true;

            } else if (cardstoloadNum.equals(input)) {
                cardstoloadSet = true;
                dirty = true;
                TextArea ta = new TextArea(cardstoloadNum);
                ta.setTitle(cardsToLoadText);
                FireScreen.getScreen().setCurrent(ta);

            } else if (leftrightText.equals(val)) {
                MapCommandKeysPanel mkp =
                    new MapCommandKeysPanel(screen, this, cmdLeftRightDone,
                                            config);
                screen.setCurrent(mkp);

            } else if (gradingkeysText.equals(val)) {
                MapKeysPanel mkp = new MapKeysPanel(screen, this, cmdKeysDone,
                                                    config);
                screen.setCurrent(mkp);

            } else if (smallText.equals(val)) {
                fontSize = Font.SIZE_SMALL;
                dirty = true;
            } else if (mediumText.equals(val)) {
                fontSize = Font.SIZE_MEDIUM;
                dirty = true;
            } else if (largeText.equals(val)) {
                fontSize = Font.SIZE_LARGE;
                dirty = true;
            }

            repaintControls();

        } else if (cmdLeftRightDone.equals(cmd)) {
            screen.setCurrent(this);
            screenSizeChanged(screen.getWidth(), screen.getHeight());
            dirty = true;

        } else if (cmdKeysDone.equals(cmd)) {
            MapKeysPanel mkp = (MapKeysPanel)c;
            for (int i=0; i < mkp.keyCode.length; ++i) {
                keys[i] = mkp.keyCode[i];
            }
            screen.setCurrent(this);
            screenSizeChanged(screen.getWidth(), screen.getHeight());
            dirty = true;

        } else if (cmdChangeDir.equals(cmd)) {
            CardDirPanel cdp = (CardDirPanel)c;
            if (cardpath == null || !cardpath.equals(cdp.cardpath)) {
                cardpath = cdp.cardpath;
                dirty = true;
            }
            screen.setCurrent(this);
            screenSizeChanged(screen.getWidth(), screen.getHeight());

        } else if (cmdLeft.equals(cmd) || cmdRight.equals(cmd)) {
            cardsToLoad = Integer.parseInt(cardstoloadNum.getValue());
            exitPanel(cmd);
        }
    }

    public void screenSizeChanged(int newWidth, int newHeight) {
        super.screenSizeChanged(newWidth, newHeight);
        buildPanel();
    }

}

