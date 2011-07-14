/*
 * VCS.java
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
package org.rstudio.studio.client.workbench.views.vcs;

import com.google.gwt.core.client.JsArray;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.inject.Provider;
import org.rstudio.core.client.command.CommandBinder;
import org.rstudio.core.client.command.Handler;
import org.rstudio.core.client.widget.ModalDialogBase;
import org.rstudio.studio.client.application.events.EventBus;
import org.rstudio.studio.client.common.GlobalDisplay;
import org.rstudio.studio.client.common.SimpleRequestCallback;
import org.rstudio.studio.client.common.vcs.StatusAndPath;
import org.rstudio.studio.client.common.vcs.VCSServerOperations;
import org.rstudio.studio.client.server.ServerError;
import org.rstudio.studio.client.server.ServerRequestCallback;
import org.rstudio.studio.client.server.Void;
import org.rstudio.studio.client.workbench.WorkbenchView;
import org.rstudio.studio.client.workbench.commands.Commands;
import org.rstudio.studio.client.workbench.views.BasePresenter;
import org.rstudio.studio.client.workbench.views.vcs.events.VcsRefreshEvent;
import org.rstudio.studio.client.workbench.views.vcs.events.VcsRefreshHandler;
import org.rstudio.studio.client.workbench.views.vcs.history.HistoryPresenter;
import org.rstudio.studio.client.workbench.views.vcs.review.ReviewPresenter;

import java.util.ArrayList;

public class VCS extends BasePresenter implements IsWidget
{
   public interface Binder extends CommandBinder<Commands, VCS> {}

   public interface Display extends WorkbenchView, IsWidget
   {
      void setItems(ArrayList<StatusAndPath> items);

      ArrayList<String> getSelectedPaths();
   }

   public interface CommitDisplay
   {
      void showModal();
   }

   @Inject
   public VCS(Display view,
              Provider<CommitDisplay> pCommitView,
              Provider<ReviewPresenter> pReviewPresenter,
              Provider<HistoryPresenter> pHistoryPresenter,
              VCSServerOperations server,
              Commands commands,
              EventBus events,
              Binder commandBinder,
              GlobalDisplay globalDisplay)
   {
      super(view);
      view_ = view;
      pCommitView_ = pCommitView;
      pReviewPresenter_ = pReviewPresenter;
      pHistoryPresenter_ = pHistoryPresenter;
      server_ = server;
      globalDisplay_ = globalDisplay;

      commandBinder.bind(commands, this);

      events.addHandler(VcsRefreshEvent.TYPE, new VcsRefreshHandler() {
         @Override
         public void onVcsRefresh(VcsRefreshEvent event)
         {
            refresh(false);
         }
      });

      refresh(false);
   }

   @Override
   public Widget asWidget()
   {
      return view_.asWidget();
   }

   @Handler
   void onVcsDiff()
   {
      ModalDialogBase dialog = new ModalDialogBase()
      {
         @Override
         protected Widget createMainWidget()
         {
            addCancelButton();

            Widget widget = pReviewPresenter_.get().asWidget();
            widget.setSize("900px", "600px");
            return widget;
         }
      };
      dialog.showModal();
   }

   @Handler
   void onVcsStage()
   {
      ArrayList<String> paths = view_.getSelectedPaths();
      if (paths.size() == 0)
         return;

      server_.vcsAdd(paths, new SimpleRequestCallback<Void>("Stage Changes")
      {
         @Override
         public void onResponseReceived(Void response)
         {
            refresh(true);
         }
      });
   }

   @Handler
   void onVcsUnstage()
   {
      ArrayList<String> paths = view_.getSelectedPaths();
      if (paths.size() == 0)
         return;

      server_.vcsUnstage(paths,
                         new SimpleRequestCallback<Void>("Unstage Changes")
                         {
                            @Override
                            public void onResponseReceived(Void response)
                            {
                               refresh(true);
                            }
                         });
   }

   @Handler
   void onVcsRevert()
   {
      ArrayList<String> paths = view_.getSelectedPaths();
      if (paths.size() == 0)
         return;

      server_.vcsRevert(paths, new SimpleRequestCallback<Void>("Revert Changes")
      {
         @Override
         public void onResponseReceived(Void response)
         {
            refresh(true);
         }
      });
   }

   @Handler
   void onVcsCommit()
   {
      pCommitView_.get().showModal();
   }

   @Handler
   void onVcsShowHistory()
   {
      pHistoryPresenter_.get().showModal();
   }

   @Handler
   void onVcsRefresh()
   {
      refresh(true);
   }

   private void refresh(final boolean showError)
   {
      server_.vcsFullStatus(new ServerRequestCallback<JsArray<StatusAndPath>>()
      {
         @Override
         public void onResponseReceived(JsArray<StatusAndPath> response)
         {
            ArrayList<StatusAndPath> list = new ArrayList<StatusAndPath>();
            for (int i = 0; i < response.length(); i++)
               list.add(response.get(i));
            view_.setItems(list);
         }

         @Override
         public void onError(ServerError error)
         {
            if (showError)
            {
               globalDisplay_.showErrorMessage("Error",
                                               error.getUserMessage());
            }
         }
      });
   }

   private final Display view_;
   private final Provider<CommitDisplay> pCommitView_;
   private final Provider<ReviewPresenter> pReviewPresenter_;
   private final Provider<HistoryPresenter> pHistoryPresenter_;
   private final VCSServerOperations server_;
   private final GlobalDisplay globalDisplay_;
}