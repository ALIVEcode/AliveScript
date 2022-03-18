# Futures versions d'aliveScript

### Version 1.2

-   changement dans les fonctions

    -   opérateur ... pour ajouter un nombre x de paramètres (spread operator en js)
    -   ex:
        ```python
        fonction abc(param1, ...autres)
            afficher typeDe(autres) # liste
        fin fonction
        ```

-   changements dans `afficher`

    -   ajout des escapes characters
        1. \n -> nouvelle ligne
        2. \b -> backspace
        3. \t -> tab
        4. \r -> carriage return
        5. \\\\ -> \\

-   changement dans precompiler

    -   permettre d'écrire tout ce qui commence par `{`, `[`, `(` ou `,`
        sur plusieurs lignes (ex: les listes, les déclarations/appels de fonctions, les index, etc.)
    -   ex:  
        lst = { 1,  
        2,  
        3,  
        4 }

-   changements dans `lire`

    -   Permettre d'afficher un message custom dans la console demandant un input
    -   Permettre de preciser une fonction à exécuter sur l'input avant d'assigner à la variable (optionnel)
        -   syntaxe actuelle: lire `var`
        -   syntaxe ajoutée: lire `fct` dans `var`
        -   ex:  
            lire entier dans var  
            afficher typeDe(var) # entier

-   documentation dans les fonctions avec  
    `(-:`  
    `documentation`  
    `:-)`
    la documentation pourra être affiché grâce à la fonction builtin `info`

### Version 1.3

-   changements à `utiliser`

    -   Permettre de changer le nom d'un module utilisé grâce à un alias
        -   syntaxe: utiliser `nomModule` alias `nouveauNom`
        -   ex:  
            utiliser Voiture alias Vo  
            Vo.vitesse = 255
    -   symbole \* comme alias pour tout utiliser sans avoir à préciser le module
        -   syntaxe: utiliser `nomModule` alias \*
        -   ex:  
            utiliser Voiture alias \*  
            vitesse = 255

### Version 2

-   Types personnalisés
-   Objets ?
-   Classes ?

-   Text blocks (avec `"""`)

    -   Comme des strings, mais l'indentation est préservée

-   Système de gestion des erreurs

    -   ajouter un mot clef pour lancer des erreurs
    -   ajouter une façon d'attraper les erreurs

-   changements dans `afficher`

    -   styliser l'output pour changer la couleur, mettre le texte en italique, en gras, etc.

-   nouveau mot clef `inclure`

    -   permet d'accéder à du code situé dans d'autres fichiers

### Version 4

-   opérateur
