(ns com.example.ui.conference-form
  (:require
    ;[com.example.model.item :as item]
    [com.example.model.session :as session]
    [com.fulcrologic.rad.picker-options :as picker-options]
    #?(:cljs [com.fulcrologic.semantic-ui.collections.form.ui-form-text-area :refer [ui-form-text-area]])
    #?(:clj  [com.fulcrologic.fulcro.dom-server :as dom]
       :cljs [com.fulcrologic.fulcro.dom :as dom])
    [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
    [com.fulcrologic.rad.control :as control]
    [com.fulcrologic.rad.form :as form]
    [com.fulcrologic.rad.form-options :as fo]
    [com.fulcrologic.rad.report :as report]
    [com.fulcrologic.rad.report-options :as ro]
    [taoensso.timbre :as log]
    [com.example.model.category :as category]
    [com.example.model.youtube-video :as youtube]
    [com.example.ui.youtube-video-forms :as youtube-forms]
    [com.example.model.conference :as conference]
    [com.example.model.youtube-playlist :as youtube-playlist]
    [com.example.ui.youtube-playlist-forms :as youtube-playlist-forms]
    [com.example.model.mutations :as mymutations]
    [com.fulcrologic.rad.routing :as rroute]
    [clojure.pprint :as pp]
    [com.fulcrologic.fulcro.routing.dynamic-routing :as dr]))

;(form/defsc-form YouTubePlaylistForm [this props]
;  {fo/id           youtube-playlist/id
;   fo/attributes   [
;                    youtube-playlist/title
;                    youtube-playlist/description]
;   fo/route-prefix "youtube-playlist-edit"
;   fo/title        "Edit YouTube Playlist"})
;;(dom/div :.ui.container.grid
;;  "Hello!"))


(comment
  (dr/resolve-path com.example.ui/Root youtube-playlist-forms/YouTubePlaylistReport
                   {:action form/edit-action
                      :id     #uuid "8a481331-eb1d-4e5b-9d19-759da23cb674"})
  ,)


(report/defsc-report ConferenceReport [this props]
  {ro/title            "Conference Report 22"
   ro/source-attribute :conference/all-conferences
   ro/row-pk           conference/id
   ro/columns          [conference/id conference/nm conference/youtube-playlists]


   ;ro/form-links       {youtube-playlist/title YouTubePlaylistForm}

   ro/links            {:conference/uuid (fn [this {:conference/keys [uuid]}]
                                           (println "ConferenceReport: click: " uuid)
                                           ;(fn [] (rroute/route-to! this AccountInvoices {:account/id (new-uuid 102)}))
                                           ; [app-or-component RouteTarget route-params]
                                           (rroute/route-to! this youtube-playlist-forms/FromYouTubeVideoReport
                                                             {:conference/uuid uuid}))}
   ;:category/label (fn [this {:category/keys [label]}]
   ;                  (control/set-parameter! this ::category label)
   ;                  (report/filter-rows! this))}

   ro/run-on-mount?    true
   ro/route            "conferences"})

;(report/defsc-report AccountInvoices [this props]
;  {ro/title                              "Customer Invoices"
;   ro/source-attribute                   :account/invoices
;   ro/row-pk                             invoice/id
;   ro/columns                            [invoice/id invoice/date invoice/total]
;   ro/column-headings                    {:invoice/id "Invoice Number"}
;
;   ro/form-links                         {:invoice/id InvoiceForm}
;   :com.fulcrologic.rad.control/controls {:account/id {:type   :uuid
;                                                       :local? true
;                                                       :label  "Account"}}
;   ;; No control layout...we don't actually let the user control it
;
;   ro/run-on-mount?                      true
;   ro/route                              "account-invoices"})

(report/defsc-report ConferencePlaylists [this props]
  {ro/title                              "Conference Playlists"
   ro/source-attribute                   :conference/youtube-playlists
   ro/row-pk                             conference/id
   ro/columns                            [conference/id conference/nm]
   ;ro/column-headings                    {:invoice/id "Invoice Number"}

   ;ro/form-links                         {:invoice/id InvoiceForm}
   ;:com.fulcrologic.rad.control/controls {:account/id {:type   :uuid
   ;                                                    :local? true
   ;                                                    :label  "Account"}}
   ;; No control layout...we don't actually let the user control it

   ro/run-on-mount?                      true
   ro/route                              "conference-playlists"}
  (dom/div
    (dom/pre (with-out-str (pp/pprint props)))))


(comment
  (comp/get-query YouTubeReport)
  (comp/get-query YouTubeForm))