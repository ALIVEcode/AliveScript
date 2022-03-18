#### Variables
- Nom: 
  - commence par une lettre (toutes les lettres unicodes)
  - ensuite, autorise les lettres (toutes les lettres unicodes), les `.`, les `_` et les chiffres
- [typage](#Typage)

- ##### Getter
  - **LES CONSTANTES NE PEUVENT PAS AVOIR DE GETTER**
  - **UNE VARIABLE NE PEUT PAS AVOIR PLUS D'UN GETTER**
  - syntaxe: 
    - ouverture: <code>get *nom*</code>
    - fermeture: `fin get`
  - utilité:
    > fonction appelée lorsque l'on veut obtenir la valeur d'une variable (par exemple pour l'afficher)
    > La valeur retournée par le get est la valeur obtenu lorsqu'on veut obtenir la valeur de la variable
  - param: *le getter prend la variable comme paramètre, elle doit donc avoir une valeur avant que le getter soit appelé*
  - type retour: *le type retourné d'un getter doit être le même que le type de la variable* 

- ##### Setter
  - **LES CONSTANTES NE PEUVENT PAS AVOIR DE SETTER**
  - **UNE VARIABLE NE PEUT PAS AVOIR PLUS D'UN SETTER**
  - syntaxe: 
    - ouverture:
      - typé: <code>set *nom*(*param*: *type*)</code>
      - pas typé: <code>set *nom*(*param*)</code>
    - fermeture: `fin set`
  - utilité:
    > fonction appelée lorsque l'on assigne une valeur à la variable
    > La valeur retournée par le set est la valeur assignée à la variable
  - type param: *le type du param est* `tout` *s'il n'est pas précisé*
  - type retour: *le type retourné d'un setter doit être le même que le type de la variable*

- ##### Déclaration:
  - variable:
    - sans type: <code>var *variable* = *valeur*</code>
    - avec type: <code>var *variable*: *type* = *valeur*</code>
  - constante:
    - sans type: <code>const *variable* = *valeur*</code>
    - avec type: <code>const *vavariabler*: *type* = *valeur*</code>

- ##### Assignement:
  - assignement variable: <code>*variable* = *valeur*</code>
  - assignement constante: <code>**ERREUR: IMPOSSIBLE DE RÉASSIGNER UNE CONSTANTE**</code>
  - assignement variable avec opération arithmétique <code>*variable* **[operation](#arithmétique)**= *valeur*</code>
    - <code>*variable* += *valeur*</code>
    - <code>*variable* -= *valeur*</code>
    - <code>*variable* *= *valeur*</code>
    - <code>*variable* /= *valeur*</code>
    - <code>*variable* %= *valeur*</code>
    - <code>*variable* ^= *valeur*</code>
    - <code>*variable* //= *valeur*</code> 
  - incrément: <code>*variable*++</code>
  - décrément: <code>*variable*--</code>

- ##### Scoping
  - deux types de scopes:
    - global (tout ce qui est défini dans le scope 'main')
    - local (tout ce qui est défini dans une fonction ou un bloc {pour, repeter, si, tant que, etc.})
  - |Variables          |Réassigner |Redéclarer           |Accéder
    ---                 |---        |---                  |---
    |même scope         |✔          |❌                  |✔
    |scope en dessous   |✔          |✔                   |✔
    |scope au dessus    |❌         |✔                   |❌

  - |Constantes         |Réassigner  |Redéclarer   |Accéder
    ---                 |---         |---          |---
    |même scope         |❌         |❌           |✔
    |scope en dessous   |❌         |✔            |✔
    |scope au dessus    |❌         |✔            |❌


