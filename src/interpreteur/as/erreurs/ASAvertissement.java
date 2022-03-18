package interpreteur.as.erreurs;

import interpreteur.data_manager.Data;

public interface ASAvertissement {
    enum AvertissementType {

    }

    class Avertissement {
        private final AvertissementType type;
        private final String message;

        Avertissement(AvertissementType type, String message) {
            this.type = type;
            this.message = message;
        }

        public Data getAsData() {
            return new Data(Data.Id.AVERTISSEMENT).addParam(type).addParam(message);
        }
    }

}
