/*
 * BottomScrollPanel.java
 *
 * Copyright (C) 2009-11 by RStudio, Inc.
 *
 * This program is licensed to you under the terms of version 3 of the
 * GNU Affero General Public License. This program is distributed WITHOUT
 * ANY EXPRESS OR IMPLIED WARRANTY, INCLUDING THOSE OF NON-INFRINGEMENT,
 * MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE. Please refer to the
 * AGPL (http://www.gnu.org/licenses/agpl-3.0.txt) for more details.
 *
 */
package org.rstudio.core.client.widget;

import com.google.gwt.event.dom.client.ScrollEvent;
import com.google.gwt.event.dom.client.ScrollHandler;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;
import org.rstudio.core.client.dom.DomUtils;

/**
 * An implementation of ScrollPanel that defaults its scroll position to the
 * bottom. If resized while the scroll position is at the bottom, the scroll
 * position will stay at the bottom (even if the panel is shrinking in size).
 */
public class BottomScrollPanel extends ScrollPanel
{
   public BottomScrollPanel()
   {
      addScrollHandler(new ScrollHandler()
      {
         public void onScroll(ScrollEvent event)
         {
            scrolledToBottom_ = getVerticalScrollPosition() + getOffsetHeight()
                == getElement().getScrollHeight();
         }
      });
   }

   public BottomScrollPanel(Widget widget)
   {
      this();
      setWidget(widget);
   }

   public boolean isScrolledToBottom()
   {
      return scrolledToBottom_;
   }

   @Override
   protected void onLoad()
   {
      super.onLoad();
      scrollToBottom();
   }

   @Override
   public void onResize()
   {
      if (scrolledToBottom_)
         scrollToBottom();
      super.onResize();
   }

   @Override
   public void scrollToBottom()
   {
      DomUtils.scrollToBottom(getElement());
      scrolledToBottom_ = true;
   }

   public void onContentSizeChanged()
   {
      if (scrolledToBottom_)
         scrollToBottom();
   }

   public void saveScrollPosition()
   {
      vScroll_ = scrolledToBottom_ ? null : getVerticalScrollPosition();
      hScroll_ = getHorizontalScrollPosition();
   }

   public void restoreScrollPosition()
   {
      if (vScroll_ == null)
         scrollToBottom();
      else
         setVerticalScrollPosition(vScroll_);

      setHorizontalScrollPosition(hScroll_);
   }

   private boolean scrolledToBottom_;
   private Integer vScroll_;
   private int hScroll_;
}
