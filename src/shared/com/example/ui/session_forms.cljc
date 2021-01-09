(ns com.example.ui.session-forms
  (:require
    ;[com.example.model.item :as item]
    #?(:clj  [com.fulcrologic.fulcro.dom-server :as dom]
       :cljs [com.fulcrologic.fulcro.dom :as dom])
    [com.fulcrologic.rad.routing :as rroute]
    [com.example.model.session :as session]
    [com.example.model.video-tag :as video-tag]
    [com.example.ui.video-tag-forms :as video-tag-form]
    [com.fulcrologic.rad.picker-options :as picker-options]
    [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
    [com.fulcrologic.rad.control :as control]
    [com.fulcrologic.rad.form :as form]
    [com.fulcrologic.rad.form-options :as fo]
    [com.fulcrologic.rad.report :as report]
    [com.fulcrologic.rad.report-options :as ro]
    [taoensso.timbre :as log]
    [com.example.model.category :as category]
    [com.example.model.account :as account]
    [clojure.pprint :as pp]
    [com.fulcrologic.fulcro.data-fetch :as df]
    [com.fulcrologic.fulcro.routing.dynamic-routing :as dr :refer [defrouter]]
    [com.fulcrologic.rad.routing.history :as history]))
    ;[com.example.client :as client]))


(form/defsc-form SessionForm [this props]
  {fo/id           session/id
   fo/attributes   [
                    ;session/id
                    session/title
                    ;session/venue
                    session/speakers
                    ;session/stype
                    session/start-time-utc
                    session/tags-2]
   ;:componentDidMount (fn [this]
   ;                     (println "SessionForm: componentDidMount"))


   fo/subforms     {:session/tags-2 {fo/ui          video-tag-form/SessionTagsSubForm
                                     fo/can-delete? (fn [_ _] true)
                                     fo/can-add?    (fn [this v]
                                                      (println "can add? v: " (clojure.pprint/pprint v))
                                                      true)}}
   ;fo/field-styles  {:item/category :pick-one}
   ;fo/field-options {:item/category {::picker-options/query-key       :category/all-categories
   ;                                  ::picker-options/query-component CategoryQuery
   ;                                  ::picker-options/options-xform   (fn [_ options] (mapv
   ;                                                                                     (fn [{:category/keys [id label]}]
   ;                                                                                       {:text (str label) :value [:category/id id]})
   ;                                                                                     (sort-by :category/label options)))
   ;                                  ::picker-options/cache-time-ms   30000}}

   ; Jakub: why is layout use symbols, where row options use attrs?
   ;fo/layout         [[:session/speakers :session/title :session/stype :session/venue
   ;                    :session/start-time-utc]
   ;                   [:session/tags]]

   fo/route-prefix "session"
   fo/title        "Edit Session"})
  ;(dom/div
  ;  (dom/pre (with-out-str (pp/pprint props)))))

(def ui-session-form (comp/factory SessionForm))

;
; right pane
;

(defsc SessionDetails [this {:session/keys [uuid speakers venue title start-time-utc
                                            tags-2] :as props}]
  {:query [:session/uuid :session/speakers :session/venue :session/title :session/tags-2
           :session/start-time-utc]
   :ident :session/uuid}

  (let [tags (->> tags-2
                  (map (fn [x]
                         (-> x
                             :session-tag-2/video-tag
                             :video-tag/name))))]
    #?(:cljs (js/console.log props))
    (dom/div
      (dom/p "Hello!")
      (dom/p "speakers: " (str ">>> " speakers "<<<"))
      (dom/p "venue: " venue)

      #?(:cljs (js/console.log "tags-2: " tags))

      (dom/p "tags: " tags))))

(def ui-session-details (comp/factory SessionDetails {:keyfn :session/uuid}))

;
; left pane
;

(comment
  (dr/resolve-path com.example.ui/Root SessionForm {:action form/edit-action
                                                    :id     "entity-id"})
  ; => ["session-list22" "session" "edit" "entity-id"]
  ; => ["session" "edit" "entity-id"]

  (dr/resolve-path SessionListManual SessionForm {:action form/edit-action
                                                  :id     "entity-id"})

  (dr/change)
  (dr/change-route! com.example.client/app
                    (dr/resolve-path SessionListManual SessionForm {:action form/edit-action
                                                                    :id     #uuid "8a481331-eb1d-4e5b-9d19-759da23cb674"}))
  ,)

(declare SessionListManual)

(defsc SessionListItem [this {:session/keys [uuid speakers venue] :as props}]
  {:query [:session/uuid :session/speakers :session/venue]
   :ident :session/uuid}
  (dom/tr
    (dom/td
      (dom/a {:href    "#"
              :onClick (fn []
                         (println "click: " props)
                         ;(form/edit! this SessionForm uuid))}
                         ; TODO: copy code from rad/routing: history
                         (let [route-params {:action form/edit-action :id     uuid}
                               path  (dr/resolve-path SessionListManual SessionForm route-params)]
                           (dr/change-route! this path)
                           ;(history/push-route! app-or-component path route-params)
                           (history/push-route! this path route-params)))}
             ;(form/edit! this SessionForm uuid))}
             ;(dr/change-route! app-or-component path route-params)
             ;(dr/change-route! this path {:action form/edit-action
             ;                             :id     uuid}))}
             ;(dr/change-route! this ["todo" "session" "edit" uuid])
             ;(df/load! this [:session/uuid uuid] SessionDetails
             ;   {:target [:component/id :session-picker :session-picker/selected-session]})}
             ; [this form-class entity-id]
             ;(form/edit! this SessionForm uuid))}
             ;(form/start-form! (comp/any->app this)
             ;                  ;nil
             ;                  ;(str (:uuid (:session-picker/selected-session params)))
             ;                  uuid SessionForm))}
             speakers))
    (dom/td venue)))
  ;(dom/li :.item
  ;    (dom/a {:href    "#"}
  ;        ;:onClick (fn []
  ;        ;           (df/load! this [:person/id id] PersonDetail
  ;        ;                       {:target ([:component/id :session-picker] :person-picker/selected-person)}))}
  ;      speakers)))

(def ui-session-list-item (comp/factory SessionListItem))


;(defsc SessionTagsSC [this {:session/keys [uuid]}]
;  {:query [:session/uuid]}
;  (dom/div
;    (dom/ul
;      (dom/li uuid))))
;"All: " (dom/input {:checked all-checked? ...})
;(dom/ul ...)))

(defsc SessionList2 [this {:session-list/keys [sessions]}]
  {:query         [{:session-list/sessions (comp/get-query SessionListItem)}]
   :ident         (fn [] [:component/id :session-list])
   :initial-state {:session-list/sessions []}}
  (dom/div
      ;(dom/pre (with-out-str (pp/pprint sessions)))
      (dom/h3 "Session List:")
      (dom/table
        (dom/thead
          (dom/tr
            (dom/th "Speakers")
            (dom/th "Venue")))
        (dom/tbody
          (map ui-session-list-item sessions)))))
       ;(dom/h3 :.ui.header "Session")
       ;(dom/ul
       ;  (map ui-session-list-item sessions)))

(def ui-session-list (comp/factory SessionList2))

(comment

  (comp/component-options SessionForm :com.fulcrologic.rad.form/id)

  ,)

(defsc EmptySC [_ _]
  {:query ['*]
   :route-segment [""]})

(defrouter SessionDetailsRouter [this props]
  {:router-targets [EmptySC SessionForm]})

(def ui-sess-det-router (comp/factory SessionDetailsRouter))

;(defsc SessionListManual [this props])
(defsc SessionListManual [this {:session-picker/keys [list selected-session router]
                                ;:session/keys [uuid speakers venue]
                                :as props}]
  {:query                      [{:session-picker/list (comp/get-query SessionList2)}
                                {:session-picker/selected-session (comp/get-query SessionDetails)}
                                {:session-picker/router (comp/get-query SessionDetailsRouter)}]
   :initial-state              {:session-picker/list {}
                                :session-picker/router {}}

   :ident                      (fn [] [:component/id :session-picker])
   ;:componentDidMount (fn [this params]
   ;                     (println "XXX componentDidMount: " this
   ;                              ;params
   ;                              (keys params)
   ;                              (:uuid (:session-picker/selected-session params)))
   ;                     (let [_ (form/start-form! (comp/any->app this)
   ;                                               ;nil
   ;                                               (:uuid (:session-picker/selected-session params))
   ;                                               SessionForm)]))
   :route-segment              ["session-list22"]}
  (let [rows (-> props)
        ;(form/form-will-enter app {:keys [action id] :as route-params} form-class)
        ;_ (rroute/route-to! this SessionForm selected-session)
        ; app id form-class
        _ (println "selected-session id: " selected-session
                   (:session/uuid selected-session))]
        ;_ (form/start-form! (comp/any->app this)
        ;                    (:session/uuid selected-session)
        ;                    SessionForm)]
        ;                    ;ui-session-form)]
        ;form (form/form-will-enter com.example.client/app
        ;                           {:action})]

    (dom/div :.ui.container
             (dom/h2 "SessionListManual")
             ;(dom/p "props:" props)
             ;(dom/pre (-> (with-out-str (pp/pprint list))))
             (dom/div :.ui.two.column.container.grid
                      ;#?(:cljs (js/console.log rows))
                      (dom/div :.column.segment.ui
                        ;(dom/h3 "Sessions")
                        (ui-session-list list))

                      (dom/div :.column.segment.ui
                        ; how to manually call (form-will-enter)
                        (dom/h3 "Session Details:")
                        (ui-sess-det-router router)

                        ;(form/start-form! this
                        ;                  (:uuid selected-session)
                        ;                  SessionForm)

                        (dom/h3 "Session Details:")
                        (ui-session-details selected-session)))
             ;{:session-list/selected-session #uuid"1b03d496-ce5e-4da5-baa9-a5b3a92555df",}
             ;                  {[:session/uuid #uuid"1b03d496-ce5e-4da5-baa9-a5b3a92555df"]
             ;                   [:session/speakers :session/venue]})))
             ;(comp/get-query SessionDetails)})))
             (dom/pre "SessionListManual: rows: \n"
               (with-out-str (pp/pprint rows))))))

(report/defsc-report SessionList [this props]
  {ro/title            "Session Report"
   ro/source-attribute :session/all-sessions
   ro/row-pk           session/id
   ;ro/query-inclusions [{:component/id :session-picker}]
   ;{:person-picker/selected-person (comp/get-query PersonDetail)}
   ;fo/query-inclusion     [:session-tag-2/video-tag]
   ro/columns          [session/speakers session/title session/stype session/venue
                        session/start-time-utc session/tags-2]
   ro/route            "session-custom-report22"}
  (let [rows (-> props
                 :ui/current-rows)]
    (dom/div :.ui.container
             (dom/h2 "Hello")
             (dom/p "props:")
             (dom/pre (-> (with-out-str (pp/pprint props))))
             (dom/div :.ui.two.column.container.grid
                      ;#?(:cljs (js/console.log rows))
                      (dom/div :.column.segment.ui
                        (dom/h3 "Session List:")
                        (dom/table
                          (dom/thead
                            (dom/tr
                              (dom/th "Speakers")
                              (dom/th "Venue")))
                          (dom/tbody
                            (map ui-session-list-item rows))))
                      (dom/div :.column.segment.ui
                        (dom/h3 "Session Details:")
                        (ui-session-form (comp/get-query SessionForm))
                        (ui-session-details (comp/get-query SessionDetails))))
                          ;{:session-list/selected-session #uuid"1b03d496-ce5e-4da5-baa9-a5b3a92555df",}
                          ;                  {[:session/uuid #uuid"1b03d496-ce5e-4da5-baa9-a5b3a92555df"]
                          ;                   [:session/speakers :session/venue]})))
                                             ;(comp/get-query SessionDetails)})))
             (dom/pre
               (with-out-str (pp/pprint rows))))))          ;session/tags]

(comment
  (comp/get-query SessionDetails))

;(defsc SessionList [this {:session-list/keys [all-sessions] :as props}]
;  {:query         [:session-list/all-sessions]
;   ;(comp/get-query PersonList)}
;   ;{:session-picker/selected-session (comp/get-query PersonDetail)}
;   ;:initial-state ()
;   :initial-state (fn [params]
;                   (df/load! SessionList [:session/all-sessions] SessionList
;                             {:target [:component/id :session-list]}))
;   :ident         (fn [] [:component/id :session-list/all-sessions])
;   :route-segment ["session-list"]}
;  (dom/div :.ui.two.column.container.grid
;           #?(:cljs (js/console.log all-sessions))
;           (dom/div :.column
;               (dom/h1 "Column 1"))
;           (dom/div :.column
;               (dom/h2 "Column 2"))))

;(defsc PersonList [this {:person-list/keys [people]}]
;  {:query         [{:person-list/people (comp/get-query PersonListItem)}]
;   :ident         (fn [] [:component/id :person-list])
;   :initial-state {:person-list/people []}}
;  (div :.ui.segment
;       (h3 :.ui.header "People")
;       (ul
;         (map ui-person-list-item people))))

;(def ui-session-list (comp/factory SessionList))
                                     ;{:keyfn :person-picker/people}))

;(defsc PersonPicker [this {:person-picker/keys [list selected-person]}]
;  {:query         [{:person-picker/list (comp/get-query PersonList)}
;                   {:person-picker/selected-person (comp/get-query PersonDetail)}]
;   :initial-state {:person-picker/list {}}
;   :ident         (fn [] [:component/id :person-picker])}
;  (div :.ui.two.column.container.grid
;       (div :.column
;               (ui-person-list list))
;       (div :.column
;               (ui-person-detail selected-person))))


(report/defsc-report CustomTopReport [this props]
  {ro/title               "Session Report"
   ro/source-attribute    :session/all-sessions
   ro/row-pk              session/id
   fo/query-inclusion     [:session-tag-2/video-tag]
   ro/columns             [session/speakers session/title session/stype session/venue
                           session/start-time-utc session/tags-2] ;session/tags]
   ro/initial-sort-params {:sort-by          :session/title
                           :sortable-columns #{:session/title :session/speakers :session/venue}
                           ; :session/stype
                           :ascending?       true}

   ro/form-links          {session/speakers SessionForm}

   ro/run-on-mount?       true
   ro/route               "session-custom-report"}
  (let [rows (-> props
                 :ui/current-rows)]
    #?(:cljs (js/console.log rows))
    (dom/div
      (dom/h3 "Hello!")

      (dom/ul
        (map (fn [x] (dom/li {:onClick (fn []
                                         (println "click: " (:session/uuid x))
                                         ;[app-or-component RouteTarget route-params]

                                         (rroute/route-to! this SessionForm
                                                           {:session/id #uuid "63827c18-5960-408f-8421-66d121a175b2"}))}
                                                           ;{:session/id (:session/uuid x)}))}
                       (:session/speakers x)))
             rows))

      (dom/pre (with-out-str (pp/pprint rows)))
      (dom/ul "List"))))

(comment
  (comp/get-query CustomTopReport))


(report/defsc-report SessionReport [this props]
  {ro/title               "Session Report"
   ro/source-attribute    :session/all-sessions
   ro/row-pk              session/id
   fo/query-inclusion     [:session-tag-2/video-tag]
   ro/columns             [session/speakers session/title session/stype session/venue
                           session/start-time-utc session/tags-2] ;session/tags]


   ;ro/row-visible?        (fn [filter-parameters row] (let [{::keys [category]} filter-parameters
   ;                                                         row-category (get row :category/label)]
   ;                                                     (or (= "" category) (= category row-category))))
   ;
   ;;; A sample server-query based picker that sets a local parameter that we use to filter rows.
   ;ro/controls            {::category {:type                          :picker
   ;                                    :local?                        true
   ;                                    :label                         "Category"
   ;                                    :default-value                 ""
   ;                                    :action                        (fn [this] (report/filter-rows! this))
   ;                                    picker-options/cache-time-ms   30000
   ;                                    picker-options/cache-key       :all-category-options
   ;                                    picker-options/query-key       :category/all-categories
   ;                                    picker-options/query-component category/Category
   ;                                    picker-options/options-xform   (fn [_ categories]
   ;                                                                     (into [{:text "All" :value ""}]
   ;                                                                           (map
   ;                                                                             (fn [{:category/keys [label]}]
   ;                                                                               {:text label :value label}))
   ;                                                                           categories))}}

   ; If defined: sort is applied to rows after filtering (client-side)
   ro/initial-sort-params {:sort-by          :session/title
                           :sortable-columns #{:session/title :session/speakers :session/venue}
                           ; :session/stype
                           :ascending?       true}


   ; TODO: add link to add tags here
   ;ro/row-actions
   ; ro/row-actions              [{:label     "Select"
   ;                                                    :action    (fn [report-instance {:tem-organization/keys [organization-number]}]
   ;                                                                 (comp/transact!
   ;                                                                   report-instance
   ;                                                                   [(mutations/set-selected-org {:orgnr organization-number})]))}]
   ;ro/row-actions              [{:label     "Select"
   ;                                                    :action    (fn [report-instance row]
   ;                                                                 (println "from session-row-actions: " row)
   ;                                                                 (js/console.log row)
   ;                                                                 (comp/transact!
   ;                                                                   report-instance
   ;                                                                   '[(mutations/set-selected-org {:orgnr organization-number})]))}]
   ro/form-links          {session/speakers SessionForm}

   ;ro/links               {:category/label (fn [this {:category/keys [label]}]
   ;                                          (control/set-parameter! this ::category label)
   ;                                          (report/filter-rows! this))}

   ro/run-on-mount?       true
   ro/route               "session-report"})
  ;(dom/div
  ;  (dom/h2 "Session Report!")
  ;  (dom/p "this: " (str this))
  ;  (dom/pre "props: " (with-out-str
  ;                       (pp/pprint props)))))


(comment
  (comp/get-query SessionReport)
  (comp/get-query SessionForm)
  (comp/get-query SessionListItem))