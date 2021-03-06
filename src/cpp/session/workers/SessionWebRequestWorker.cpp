/*
 * SessionWebRequestWorker.cpp
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

#include <core/Error.hpp>

#include <session/SessionWorkerContext.hpp>

using namespace core;

namespace session {
namespace workers {
namespace web_request {

namespace {

Error webRequest(const json::JsonRpcRequest& request,
                 json::JsonRpcResponse* pResponse)
{



   return Success();
}

} // anonymouys namespace

Error initialize()
{
   return worker_context::registerWorkerRpcMethod("web_request", webRequest);
}

} // namespace web_request
} // namespace workers
} // namespace session




