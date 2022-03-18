# Features intéressantes qu'ont d'autres langages de prog


ex:  
```js
var a = 200
fonction abc()
   afficher a
fin fonction

var z = \{1, 2, 3}
```  

### Stack:
- a -> 200
- abc -> pointe à 1.
- z -> pointe à 2.


### Memory heap:
1. fonction abc() ...
2. \{1, 2, 3}


### Notes:
- Les valeurs primitives sont notées directement dans le stack
  - Valeur primitives:
    - nombres (entier et decimal)
    - nul
    - booleen
    - texte
- Les fonctions et les arrays sont notées dans le heap et ce sont seulement les références à ces éléments qui sont enregistrées dans le stack