/*
* Adama Platform and Language
* Copyright (C) 2021 - 2023 by Adama Platform Initiative, LLC
* 
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU Affero General Public License as published
* by the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
* 
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU Affero General Public License for more details.
* 
* You should have received a copy of the GNU Affero General Public License
* along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/
package org.adamalang.common.keys;

import io.jsonwebtoken.Jwts;
import org.junit.Test;

import java.security.PrivateKey;
import java.security.PublicKey;

public class RSAPemKeyTests {
  @Test
  public void jwt() throws Exception {
    PrivateKey privateKey = RSAPemKey.privateFrom("MIIJQwIBADANBgkqhkiG9w0BAQEFAASCCS0wggkpAgEAAoICAQDFUQFcp8VabV+u\n" + "tnrCeRqzJ4B42S2j7SkIkDOfm/9kdpIGp5ob9PDii/Bq89zd5fep/K8uVv44T8yC\n" + "Y1QZOkIAnkAwRUAFaCul0KowKp7gPb2HGaBIcWHrPqG86OuWq3gVNmd3sW4eJ0dA\n" + "73Wz3u7T1YVZN5VPsciSR6M/wgKXtGsH6BzqxeHrOH2Q/QpxHW4mRfTqf+QbmNhG\n" + "vAg7nsuhPRTqkrCmLu4C0dDz37Tts5knBYQl/CIvrGIFms6sPwhdrTjzQezLzuKk\n" + "Pgu6OhfPQo6Z+Z/1VnfuIu8ecxCl0gY+ZxSwcEog8kOb9hEsLDBB5P4yCRoeYpGu\n" + "TbzaZzeeNekn3Fjw9ndM0T/AXgdSskXQdre8eNzwRma2Lm7NQNSiHkFHNrU0Y+j4\n" + "lfbQo6nj7wtli5fpa5voMZwCz8umBWe3mEl+l9/vuWRh07nmI+DajTutZts8emIa\n" + "uLfGL/hQTWLjdDLrdaqKPBloRrVB4045QAWt9rbO2Q+BLjbwujWDyjrGOSXyt8dA\n" + "r4aQsIoXBDVb8yMouAznTMUNc7R3kvFa+YLn06m27ee8XFc4Pk1c0Mqdp7pbEvpZ\n" + "bAsdEit2xWF8VFwI1HOzeZ8ubHiAKw3WW69YsipOFu+hNWK6iDTIOAZcU67fVUj4\n" + "/MhLZpw+/CFvKeuUTRZIfhtHLLfoLwIDAQABAoICADLEetM6H1760SmEreUnf6Uk\n" + "vrtZNZL9zBb18zJX0sgocb3glZgn12qSktMR13wIVUaZSwFjRJl8fqP1poevI5hB\n" + "H0bMKoK5oLFTFNa7wJD1kbQW9dRtx8HdJYQTcboceBMNImX7ctezR3P/qIsVCuHa\n" + "CCtCT9pnZbGtWOVop34c34TzRa1qKIx6xmPtlygvaE3UpgVHzosDMnJQotWkL8RW\n" + "kPEhuO2pGXgEP/zfTA/0q60MkEmtq6YUdkv23sIWnbJshUChb6mom0ui4yWRGNss\n" + "xr/7KBrZmpXcyyVMebYQaabYoswauBft/VM9ZoG1BN2Pa4+4I845kj4EpliTQg2x\n" + "C0VqW7inKkzgbr3E45QgTjdexBgW3mbvyiFmlgx2at1m3tA3qAsE6vjfnwp+qPYN\n" + "kcNK2ZwIrEO0Vfe/YpZ6ot2g/+9lbIiscAkyt98GAmSoFSZx40y3z9wR9VQLEj1N\n" + "zak03+YRNRyLXMdtac1cB6Gm83LIZFXZx/U/doCvsj0cD9Q5Q5ye4/KYn5soIqOQ\n" + "omfvz2xC8OACWsmatkfkZwk0LD7BDezKrI0/ho2t9BdDtMJXyvsLZl2rExrFynoA\n" + "iK/Ko5No0Tq2/GEQjDXrfDgk817LRv8JB+xVq24p2WUM1Zq5JDvcTk4FmU6EO5TR\n" + "CyN/uSR1Le0tbVARVf3RAoIBAQDWJrjgy/DHHj2JpQs3YnZL108RztkBxa7CZEOF\n" + "CWki60Zkyv2E2Sp9nnUn/ZnEHeHgUAc8lxHSPV81djDANCvdjtJgmOHWRFhXMMtj\n" + "24bnp2sD64e5LIz88iPtFsnAo82SL0dIFWuXmeB5RWaEwZ8v/Z26Ca8oAKfb8+mJ\n" + "1EBtiGJHfYjJaWIqVdXL4sLssY7SzO5ETpuSkVqm1QciswnXGzFId0gwhpEBmOGH\n" + "lGDLiUKmerwycUf7LegSz1JWf4deLB/LunIlja7OoLQyV7x6j4+dqAwozNEGWKz7\n" + "HmR28jtYxi0NU9nWrk+6ICEAibx6QL8ZeEWmWXwEVZkORA79AoIBAQDr4BehcXHy\n" + "JlIR1dLSg88xiDqR8qcd9vORahAHMwBFwm8pWcJqWRkofClzn114eAxIPo8IosqC\n" + "A8VevrupeNeBvCq5G8LnQqc/0Ywh0QvL4sv1I5hRUYytAlnv5IvWr3IPidv1kIEo\n" + "A6QD2RiWqgVqsv38RCzH++zmfUnbpuoj4hqk0upngql5TZC22YVxC8LGxQABIHXp\n" + "jpLDWS0hH4Kk7YyZfb+PgwKR9E9l2Aw+a/eNofwMeo4u8e0fC01/cMn6rTQo31Fy\n" + "bw8E0y1sDsBanxNB4H9bNwMuyZSlwnDjz8Kc9LTGS6RZ+4SNNgZsmV8QGp1Beo+/\n" + "RBDGQIK/xrmbAoIBAQDI0BDHg5YqMdpEy4ZkDimd3q3fphbqSYQh90E1bGixfOxK\n" + "serfe4ETwNZdhLp/JAJehMz0bTkh67ju+gCnC4Uiwr3WJSaI9Rfd0vy2i/HKIr0A\n" + "NQnVg8hFnY5A0gCoazHm5fyyxJta0smjDoge7y2mI+2WuWQNX+gIIoYZX/Z2f9Cg\n" + "9z+glvideFX+VYqxx6KFW/QGC6xo2XKnRNXnHPqaQn6JVIlWL6mkVMcPivzlf7YH\n" + "RMpbhsyX5ilcEhtEz1uR3FCIizJksqJ8UkgHeSxdxWdH0IBudfmAOQEkjhBpVaZ/\n" + "DX7Lo5h/bn+GqoAn8ADUpwhMCvNNFVZs/hODRCDFAoIBAFsVNqrN3t1N9OUUN4u5\n" + "AFo1K8DN/cBxaw6c8UGGbTWC5mAGXC+q5i8e+va99F8fpEaFecAw9JgOEroKVvcT\n" + "RczAN5MxaCrbLzBSn5UzXyXFF1M7AodsQTFRth8Plvw41YewiXKi8WzdMwmVXIxZ\n" + "bSwNoEw4mz5CRvGeMdYS91kR5438MC32Y7EGxboF9yR3ESimS+EjgsTEnGG43tdq\n" + "jo1XebKPv1gQk/5+Y+aE7f1wvbBAe87pyFvSaelR+QQcLrKRDZI24gGZZyhKz8Bp\n" + "Vdfok4wdv7AeZyk7CB6WHfeqc5EeYWwtK3Lf0Aijcwmek/no/Q6a4EVQaFXV9WdO\n" + "ur8CggEBAIIBOV3REp+xvjU4P8o7PuZtb8LCK9V24nZYVbiOUHGZraeHNM2oaVQb\n" + "4nB0i6abs3RsWrltrgCkeRrdxHJWGKB+hVlgJzFqFbGEmtJY7ttjYwpBN43xv0tj\n" + "Uu8Or04Vrle4uuWC35bkkXAu5Bkfb7y1OR3+DfhsL3D8ePlBDCpS8NJUKJ6m7bmi\n" + "pen2Yu+38ktMhzZgUdwMuVQlCySmZVA/Gz+4iiH///oL5fzkTn/ta84AwSkbFObl\n" + "mJqtOM79DbD/iGa8YHDaMiWAaGaT3bwAf/fhqqseoy9or3FJ/w8ElVJTeMUcm6Uo\n" + "Ij6YBpOmGBPZ+yz8AAYERRgKYdh0eLY=");
    String token = Jwts.builder().subject("agent").issuer("myissuer").signWith(privateKey).compact();
    PublicKey publicKey = RSAPemKey.publicFrom("MIICIjANBgkqhkiG9w0BAQEFAAOCAg8AMIICCgKCAgEAxVEBXKfFWm1frrZ6wnka\n" + "syeAeNkto+0pCJAzn5v/ZHaSBqeaG/Tw4ovwavPc3eX3qfyvLlb+OE/MgmNUGTpC\n" + "AJ5AMEVABWgrpdCqMCqe4D29hxmgSHFh6z6hvOjrlqt4FTZnd7FuHidHQO91s97u\n" + "09WFWTeVT7HIkkejP8ICl7RrB+gc6sXh6zh9kP0KcR1uJkX06n/kG5jYRrwIO57L\n" + "oT0U6pKwpi7uAtHQ89+07bOZJwWEJfwiL6xiBZrOrD8IXa0480Hsy87ipD4LujoX\n" + "z0KOmfmf9VZ37iLvHnMQpdIGPmcUsHBKIPJDm/YRLCwwQeT+MgkaHmKRrk282mc3\n" + "njXpJ9xY8PZ3TNE/wF4HUrJF0Ha3vHjc8EZmti5uzUDUoh5BRza1NGPo+JX20KOp\n" + "4+8LZYuX6Wub6DGcAs/LpgVnt5hJfpff77lkYdO55iPg2o07rWbbPHpiGri3xi/4\n" + "UE1i43Qy63WqijwZaEa1QeNOOUAFrfa2ztkPgS428Lo1g8o6xjkl8rfHQK+GkLCK\n" + "FwQ1W/MjKLgM50zFDXO0d5LxWvmC59Optu3nvFxXOD5NXNDKnae6WxL6WWwLHRIr\n" + "dsVhfFRcCNRzs3mfLmx4gCsN1luvWLIqThbvoTViuog0yDgGXFOu31VI+PzIS2ac\n" + "PvwhbynrlE0WSH4bRyy36C8CAwEAAQ==");
    Jwts.parser().verifyWith(publicKey).build().parse(token);
  }
}
