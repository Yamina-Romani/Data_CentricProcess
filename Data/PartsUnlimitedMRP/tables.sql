
Structure de la table catalog

|------
|Colonne|Type|Null|Valeur par défaut
|------
|//**catalogId**//|varchar(50)|Non|
|skuNumber|text|Non|
|description|text|Non|
|price|double|Non|
|inventory|int(11)|Non|
|leadTime|int(11)|Non|

== Structure de la table dealers
|------
|Colonne|Type|Null|Valeur par défaut
|------
|//**dealerId**//|varchar(50)|Non|
|name|varchar(100)|Non|
|contact|varchar(255)|Non|
|address|text|Non|
|email|varchar(20)|Non|
|phone|varchar(100)|Non|

== Structure de la table orders
|------
|Colonne|Type|Null|Valeur par défaut
|------
|//**orderDetailsId**//|varchar(50)|Non|
|orderId|varchar(50)|Non|
|quoteId|int(50)|Non|
|orderDate|varchar(100)|Non|
|OrderStatus|text|Non|
|OrderEventInfo|text|Non|

|------
|Colonne|Type|Null|Valeur par défaut
|------
|//**quoteDetailsId**//|varchar(50)|Non|
|quoteId|varchar(50)|Non|
|validUntil|varchar(100)|Non|
|customerName|varchar(100)|Non|
|dealerName|varchar(100)|Non|
|QuoteItemInfo|text|Non|
|totalCost|double|Non|
|discount|double|Non|
|city|varchar(100)|Non|
|postalCode|varchar(100)|Non|
|state|varchar(100)|Non|

== Structure de la table shipments
|------
|Colonne|Type|Null|Valeur par défaut
|------
|//**shipmentId**//|varchar(50)|Non|
|orderId|varchar(50)|Non|
|ShipmentEventInfo|text|Non|
|DeliveryAddress|text|Non|
|contactName|varchar(100)|Non|
|primaryContactPhone|text|Non|
|alternateContactPhone|text|Non|
