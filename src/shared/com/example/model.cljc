(ns com.example.model
  (:require
    [com.example.model.timezone :as timezone]
    [com.example.model.account :as account]
    [com.example.model.item :as item]
    [com.example.model.invoice :as invoice]
    [com.example.model.line-item :as line-item]
    [com.example.model.address :as address]
    [com.example.model.category :as category]
    [com.example.model.file :as m.file]
    [com.example.model.sales :as sales]
    ;
    [com.example.model.session :as session]
    [com.example.model.youtube-video :as youtube]
    [com.example.model.youtube-playlist :as youtube-playlist]
    [com.example.model.video-tag :as vtag]
    [com.example.model.session-tag-2 :as session-tag-2]
    ;
    [com.fulcrologic.rad.attributes :as attr]))

(def all-attributes (vec (concat
                           account/attributes
                           address/attributes
                           category/attributes
                           item/attributes
                           invoice/attributes
                           line-item/attributes
                           m.file/attributes
                           sales/attributes
                           timezone/attributes
                           session/attributes
                           youtube/attributes
                           youtube-playlist/attributes
                           vtag/attributes
                           session-tag-2/attributes)))

; XXX: delete this?  no, we need it
(def all-video-attributes (vec (concat
                                 session/attributes
                                 youtube/attributes
                                 youtube-playlist/attributes
                                 vtag/attributes
                                 session-tag-2/attributes)))



(def all-attribute-validator (attr/make-attribute-validator all-attributes))
