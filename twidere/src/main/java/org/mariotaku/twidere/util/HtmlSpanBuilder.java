/*
 *                 Twidere - Twitter client for Android
 *
 *  Copyright (C) 2012-2015 Mariotaku Lee <mariotaku.lee@gmail.com>
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.mariotaku.twidere.util;

import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.URLSpan;

import org.apache.commons.lang3.StringUtils;
import org.attoparser.AttoParseException;
import org.attoparser.IAttoParser;
import org.attoparser.markup.MarkupAttoParser;
import org.attoparser.markup.html.AbstractStandardNonValidatingHtmlAttoHandler;
import org.attoparser.markup.html.HtmlParsingConfiguration;
import org.attoparser.markup.html.elements.IHtmlElement;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by mariotaku on 15/11/4.
 */
public class HtmlSpanBuilder {

    private static final IAttoParser PARSER = new MarkupAttoParser();

    public static Spanned fromHtml(String html) {
        final HtmlParsingConfiguration conf = new HtmlParsingConfiguration();
        final HtmlSpanHandler handler = new HtmlSpanHandler(conf);
        try {
            PARSER.parse(html, handler);
        } catch (AttoParseException e) {
            throw new RuntimeException(e);
        }
        return handler.getText();
    }

    private static void applyTag(SpannableStringBuilder sb, int start, int end, TagInfo info) {
        final Object span = createSpan(info);
        if (span == null) return;
        sb.setSpan(span, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    }

    private static Object createSpan(TagInfo info) {
        switch (info.name) {
            case "a": {
                return new URLSpan(info.getAttribute("href"));
            }
        }
        return null;
    }

    private static int lastIndexOfTag(List<TagInfo> info, String name) {
        for (int i = info.size() - 1; i >= 0; i--) {
            if (StringUtils.equals(info.get(i).name, name)) {
                return i;
            }
        }
        return -1;
    }

    private static class TagInfo {
        final int start;
        final String name;
        final Map<String, String> attributes;

        public TagInfo(int start, String name, Map<String, String> attributes) {
            this.start = start;
            this.name = name;
            this.attributes = attributes;
        }

        public String getAttribute(String attr) {
            return attributes.get(attr);
        }
    }

    private static class HtmlSpanHandler extends AbstractStandardNonValidatingHtmlAttoHandler {
        private final SpannableStringBuilder sb;
        List<TagInfo> tagInfo;

        public HtmlSpanHandler(HtmlParsingConfiguration conf) {
            super(conf);
            this.sb = new SpannableStringBuilder();
            tagInfo = new ArrayList<>();
        }

        @Override
        public void handleText(char[] buffer, int offset, int len, int line, int col) throws AttoParseException {
            sb.append(HtmlEscapeHelper.unescape(new String(buffer, offset, len)));
        }

        @Override
        public void handleHtmlCloseElement(IHtmlElement element, String elementName, int line, int col) throws AttoParseException {
            final int lastIndex = lastIndexOfTag(tagInfo, elementName);
            if (lastIndex != -1) {
                TagInfo info = tagInfo.get(lastIndex);
                applyTag(sb, info.start, sb.length(), info);
                tagInfo.remove(lastIndex);
            }
        }

        @Override
        public void handleHtmlOpenElement(IHtmlElement element, String elementName, Map<String, String> attributes, int line, int col) throws AttoParseException {
            tagInfo.add(new TagInfo(sb.length(), elementName, attributes));
        }

        public Spanned getText() {
            return sb;
        }
    }
}