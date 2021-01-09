(ns com.example.ui
  (:require
    #?@(:cljs [[com.fulcrologic.semantic-ui.modules.dropdown.ui-dropdown :refer [ui-dropdown]]
               [com.fulcrologic.semantic-ui.modules.dropdown.ui-dropdown-menu :refer [ui-dropdown-menu]]
               [com.fulcrologic.semantic-ui.modules.dropdown.ui-dropdown-item :refer [ui-dropdown-item]]])
    #?(:clj  [com.fulcrologic.fulcro.dom-server :as dom :refer [div label input]]
       :cljs [com.fulcrologic.fulcro.dom :as dom :refer [div label input]])
    [com.example.ui.account-forms :refer [AccountForm AccountList]]
    [com.example.ui.invoice-forms :refer [InvoiceForm InvoiceList AccountInvoices]]
    [com.example.ui.item-forms :refer [ItemForm InventoryReport]]
    [com.example.ui.line-item-forms :refer [LineItemForm]]
    [com.example.ui.session-forms :refer [SessionForm SessionReport CustomTopReport ui-session-list SessionList
                                          SessionListManual]]
    [com.example.ui.youtube-video-forms :refer [YouTubeReportAll YouTubeForm YouTubeReportByPlaylist]]
    [com.example.ui.youtube-playlist-forms :refer [YouTubePlaylistReport YouTubePlaylistForm]]
    [com.example.ui.video-tag-forms :refer [VideoTagReport VideoTagForm]]
    [com.example.ui.conference-form :refer [ConferenceReport ConferencePlaylists]]
    [com.example.ui.login-dialog :refer [LoginForm]]
    [com.example.ui.sales-report :as sales-report]
    [com.example.ui.dashboard :as dashboard]
    [com.fulcrologic.fulcro.application :as app]
    [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
    [com.fulcrologic.fulcro.dom.html-entities :as ent]
    [com.fulcrologic.fulcro.routing.dynamic-routing :as dr :refer [defrouter change-route-relative!
                                                                   change-route!]]
    [com.fulcrologic.rad.authorization :as auth]
    [com.fulcrologic.rad.form :as form]
    [com.fulcrologic.rad.ids :refer [new-uuid]]
    [com.fulcrologic.rad.routing :as rroute]
    [com.example.model.mutations :as mymutations]
    [taoensso.timbre :as log]))

; (com.fulcrologic.fulcro.routing.dynamic-routing/current-route com.example.client/app)

(defsc LandingPage [this props]
  {:query         ['*]
   :ident         (fn [] [:component/id ::LandingPage])
   :initial-state {}
   :route-segment ["landing-page"]}
  (dom/div "Welcome to the Demo. Please log in. Gene was here."))

;; This will just be a normal router...but there can be many of them.
(defrouter MainRouter [this {:keys [current-state route-factory route-props]}]
  {:always-render-body? true
   :router-targets      [LandingPage ItemForm InvoiceForm InvoiceList AccountList AccountForm AccountInvoices
                         sales-report/SalesReport InventoryReport
                         sales-report/RealSalesReport
                         SessionReport SessionForm  SessionList SessionListManual
                         YouTubeReportAll YouTubeForm YouTubeReportByPlaylist
                         YouTubePlaylistReport YouTubePlaylistForm
                         VideoTagReport VideoTagForm CustomTopReport
                         ConferenceReport ConferencePlaylists
                         dashboard/Dashboard]}
  ;; Normal Fulcro code to show a loader on slow route change (assuming Semantic UI here, should
  ;; be generalized for RAD so UI-specific code isn't necessary)
  (dom/div
    (dom/div :.ui.loader {:classes [(when-not (= :routed current-state) "active")]})
    (when route-factory
      (route-factory route-props))))

(def ui-main-router (comp/factory MainRouter))

(auth/defauthenticator Authenticator {:local LoginForm})

(def ui-authenticator (comp/factory Authenticator))

(defsc Root [this {::auth/keys [authorization]
                   ::app/keys  [active-remotes]
                   :keys       [authenticator router]}]
  {:query         [{:authenticator (comp/get-query Authenticator)}
                   {:router (comp/get-query MainRouter)}
                   ::app/active-remotes
                   ::auth/authorization]
   :initial-state {:router        {}
                   :authenticator {}}}
  (let [logged-in? (= :success (some-> authorization :local ::auth/status))
        busy?      (seq active-remotes)
        username   (some-> authorization :local :account/name)]
    (dom/div
      (div :.ui.top.menu
        (div :.ui.item "Demo")
        (when logged-in?
          #?(:cljs
             (comp/fragment
               (ui-dropdown {:className "item" :text "Account"}
                 (ui-dropdown-menu {}
                   (ui-dropdown-item {:onClick (fn [] (rroute/route-to! this AccountList {}))} "View All")
                   (ui-dropdown-item {:onClick (fn [] (form/create! this AccountForm))} "New")))
               (ui-dropdown {:className "item" :text "Inventory"}
                 (ui-dropdown-menu {}
                   (ui-dropdown-item {:onClick (fn [] (rroute/route-to! this InventoryReport {}))} "View All")
                   (ui-dropdown-item {:onClick (fn [] (form/create! this ItemForm))} "New")))
               (ui-dropdown {:className "item" :text "Invoices"}
                 (ui-dropdown-menu {}
                   (ui-dropdown-item {:onClick (fn [] (rroute/route-to! this InvoiceList {}))} "View All")
                   (ui-dropdown-item {:onClick (fn [] (form/create! this InvoiceForm))} "New")
                   (ui-dropdown-item {:onClick (fn [] (rroute/route-to! this AccountInvoices {:account/id (new-uuid 102)}))} "Invoices for Account 101")))
               (ui-dropdown {:className "item" :text "Reports"}
                 (ui-dropdown-menu {}
                   (ui-dropdown-item {:onClick (fn [] (rroute/route-to! this dashboard/Dashboard {}))} "Dashboard")
                   (ui-dropdown-item {:onClick (fn [] (rroute/route-to! this sales-report/RealSalesReport {}))} "Sales Report")))
               (ui-dropdown {:className "item" :text "Sessions"}
                 (ui-dropdown-menu {}
                   (ui-dropdown-item {:onClick (fn [] (rroute/route-to! this SessionReport {}))} "View Sessions")
                   (ui-dropdown-item {:onClick (fn [] (rroute/route-to! this VideoTagReport {}))} "View Tags")
                   (ui-dropdown-item {:onClick (fn [] (form/create! this VideoTagForm))} "New Tag 2")
                   (ui-dropdown-item {:onClick (fn [] (rroute/route-to! this SessionList {}))} "Custom")
                   (ui-dropdown-item {:onClick (fn []
                                                 ; rroute/route-to! [app-or-component RouteTarget route-params]
                                                 (rroute/route-to! this SessionListManual {}))
                                                 ;(dr/change-route! this SessionListManual))
                                      ,} "Custom Manual")))
               (ui-dropdown {:className "item" :text "Conferences"}
                 (ui-dropdown-menu {}
                   (ui-dropdown-item {:onClick (fn [] (rroute/route-to! this ConferenceReport {}))} "Conferences")
                   (ui-dropdown-item {:onClick (fn [] (rroute/route-to! this YouTubePlaylistReport {}))} "View YouTube Playlists")
                   (ui-dropdown-item {:onClick (fn [] (rroute/route-to! this YouTubeReportAll {}))} "View YouTube Videos"),
                   (ui-dropdown-item {:onClick (fn [] (rroute/route-to! this AccountInvoices {:account/id (new-uuid 101)}))} "Invoices for Account 101")
                   (ui-dropdown-item {:onClick (fn [] (rroute/route-to! this ConferencePlaylists {:conference/uuid #uuid"2e24aa89-48ef-4a4c-879f-f1900ada35ea"}))}
                                     "Playlists for Vegas 2019")))
               (ui-dropdown {:className "item" :text "YouTube Channel"}
                 (ui-dropdown-menu {}
                   (ui-dropdown-item {:onClick
                                      (fn []
                                        (comp/transact! this [(mymutations/fetch-from-youtube-playlists {:abc 123})]))}
                                        ;(rroute/route-to! this FromYouTubePlaylists {}))}
                                     "Playlists")
                   (ui-dropdown-item {:onClick (fn [] (rroute/route-to! this AccountInvoices {:account/id (new-uuid 101)}))} "Invoices for Account 101"))))))
        (div :.right.menu
          (div :.item
            (div :.ui.tiny.loader {:classes [(when busy? "active")]})
            ent/nbsp ent/nbsp ent/nbsp ent/nbsp)
          (if logged-in?
            (comp/fragment
              (div :.ui.item
                (str "XX Logged in as " username))
              (div :.ui.item
                (dom/button :.ui.button {:onClick (fn []
                                                    ;; TODO: check if we can change routes...
                                                    (rroute/route-to! this LandingPage {})
                                                    (auth/logout! this :local))}
                  "Logout")))
            (div :.ui.item
              (dom/button :.ui.primary.button {:onClick #(auth/authenticate! this :local nil)}
                "Login")))))
      (div :.ui.container.segment
        (ui-authenticator authenticator)
        (ui-main-router router)))))

(def ui-root (comp/factory Root))

(comment
  (comp/transact! ui-root [(mymutations/fetch-from-youtube-playlists {:abc 123})]))

