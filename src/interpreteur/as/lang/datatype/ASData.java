package interpreteur.as.lang.datatype;


import interpreteur.as.experimental.annotations.Experimental;
import interpreteur.as.experimental.annotations.ExperimentalStage;

@Experimental(stage = ExperimentalStage.PROTOTYPE)
public class ASData implements ASObjet<Object> {

    public ASData() {

    }

    @Override
    public Object getValue() {
        return null;
    }

    @Override
    public boolean boolValue() {
        return false;
    }

    @Override
    public String obtenirNomType() {
        return null;
    }
}



/*
 * structure Personne
 *    var nom: texte
 *    var age: entier
 * fin structure
 *
 * const personne: Personne = Personne { nom: "John", age: 30 }
 *
 * afficher personne.nom
 * afficher personne.age
 *
 * afficher Personne.nom(personne)
 * afficher Personne.age(personne)
 *
 * afficher personne::nom
 * afficher personne::age
 *
 * afficher personne->nom
 * afficher personne->age
 *
 * afficher personne=>nom
 * afficher personne=>age
 *
 * afficher personne{nom}
 * afficher personne{age}
 *
 */

/*
 *
 * structure Personnne
 *     nom: texte
 *     age: entier
 *     amis: liste = []
 *
 *     fonction creer(nom: texte, age: entier, amis: liste = [])
 *         retourner Personne { nom, age, amis }
 *     fin fonction
 *
 * fin structure
 *
 * const personne: Personne = Personne.creer("John", 30)
 *
 *
 * const personne: Personne = Personne { nom:"John", age:30 }
 * const personne: Personne = Personne("John", 30)
 *
 * afficher personne->nom
 *
 * utiliser Ai
 *
 * modele: Ai.Modele = Ai.creerModele()
 *
 * tant que modele->erreur > 3
 *     Ai.evaluer(modele)
 *     Ai.optimiser(modele)
 *     afficher modele->erreur
 * fin tant que
 *
 *
 * Ai.evaluer(modele)
 *
 * typeDe(modele) == "Ai.Modele"
 *
 *
 *
 * # 1: est-ce que les structures sont des dictionnaires boostés? Oui
 * # 2: est-ce que la syntaxe (->) s'applique aussi aux dictionnaires? Maybe
 * # 3: est-ce que les fonctions d'une structure prennent elle-même comme 1er arg?
 *
 * (: # afficher une structure
 * Modele { erreur: 12.2, type: 'NN' }
 * :)
 *
 *
 *
 *
 * structure Animal
 *   nom: texte
 *   age: entier
 *   couleur: texte
 *   taille: entier
 *
 *   fonction creer(nom: texte, age: entier, couleur: texte, taille: entier) -> Animal
 *        retourner Animal { nom, age, couleur, taille }
 *   fin fonction
 *
 *   fonction getNom(animal: Animal) -> texte
 *       retourner animal->nom
 *   fin fonction
 *
 *   fonction getAge(instance: Animal) -> entier
 *      retourner instance->age
 *   fin fonction
 * fin structure
 *
 * const animal: Animal = Animal.creer("Chat", 3, "roux", 10)
 * afficher animal->getNom()
 *
 */


