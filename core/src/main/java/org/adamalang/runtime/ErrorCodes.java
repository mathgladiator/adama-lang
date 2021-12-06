/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE'
 * which is in the root directory of the repository. This file is part of the 'Adama'
 * project which is a programming language and document store for board games.
 * 
 * See http://www.adama-lang.org/ for more information.
 * 
 * (c) 2020 - 2021 by Jeffrey M. Barber (http://jeffrey.io)
*/
package org.adamalang.runtime;

/** error codes for things that go bump with the core package */
public class ErrorCodes {
  public final static int DURABLE_LIVING_DOCUMENT_STAGE_FRESH_DRIVE = 123392;
  public final static int DURABLE_LIVING_DOCUMENT_STAGE_FRESH_PERSIST = 198657;
  public final static int DURABLE_LIVING_DOCUMENT_STAGE_CONSTRUCT_DRIVE = 130568;
  public final static int DURABLE_LIVING_DOCUMENT_STAGE_CONSTRUCT_PERSIST = 134152;
  public final static int DURABLE_LIVING_DOCUMENT_STAGE_LOAD_DRIVE = 143880;
  public final static int DURABLE_LIVING_DOCUMENT_STAGE_LOAD_READ = 101386;
  public final static int DURABLE_LIVING_DOCUMENT_STAGE_ATTACH_PRIVATE_VIEW = 138255;
  public static final int LIVING_DOCUMENT_TRANSACTION_NO_COMMAND_FOUND = 194575;
  public static final int LIVING_DOCUMENT_TRANSACTION_UNRECOGNIZED_FIELD_PRESENT = 184335;
  public static final int LIVING_DOCUMENT_TRANSACTION_ALREADY_CONNECTED = 115724;
  public static final int LIVING_DOCUMENT_TRANSACTION_ALREADY_CONSTRUCTED = 132111;
  public static final int LIVING_DOCUMENT_TRANSACTION_CANT_DISCONNECT_DUE_TO_NOT_CONNECTED = 145423;
  public static final int LIVING_DOCUMENT_TRANSACTION_CANT_SEND_NO_CHANNEL = 160268;
  public static final int LIVING_DOCUMENT_TRANSACTION_CANT_SEND_NO_MESSAGE = 184332;
  public static final int LIVING_DOCUMENT_TRANSACTION_CANT_SEND_NOT_CONNECTED = 143373;
  public static final int LIVING_DOCUMENT_TRANSACTION_CANT_ATTACH_NOT_CONNECTED = 125966;
  public static final int LIVING_DOCUMENT_TRANSACTION_CLIENT_REJECTED = 184333;
  public static final int LIVING_DOCUMENT_TRANSACTION_NO_CLIENT_AS_WHO = 122896;
  public static final int LIVING_DOCUMENT_TRANSACTION_NO_LIMIT = 146448;
  public static final int LIVING_DOCUMENT_TRANSACTION_NO_CONSTRUCTOR_ARG = 196624;
  public static final int LIVING_DOCUMENT_TRANSACTION_NO_TIMESTAMP = 143889;
  public static final int LIVING_DOCUMENT_TRANSACTION_NO_VALID_COMMAND_FOUND = 132116;
  public static final int LIVING_DOCUMENT_TRANSACTION_NO_ASSET = 143380;
  public static final int LIVING_DOCUMENT_TRANSACTION_NO_PATCH = 193055;
  public static final int LIVING_DOCUMENT_TRANSACTION_MESSAGE_ALREADY_SENT = 143407;
  public static final int LIVING_DOCUMENT_TRANSACTION_EXPIRE_LIMIT_MUST_BE_POSITIVE = 122412;
  public static final int FACTORY_CANT_BIND_JAVA_CODE = 198174;
  public static final int FACTORY_CANT_COMPILE_JAVA_CODE = 180258;
  public static final int FACTORY_CANT_CREATE_OBJECT_DUE_TO_CATASTROPHE = 115747;
  public static final int CATASTROPHIC_DOCUMENT_FAILURE_EXCEPTION = 144416;
  public static final int SERVICE_DOCUMENT_ALREADY_CREATED = 130092;

  public static final int USERLAND_CANT_COMPILE_ADAMA_SCRIPT = 144417;



  /**
   * 133120
   * 113708
   * 195116
   * 113711
   * 134195
   * 111155
   * 198705
   * 116787
   * 144944
   * 106546
   * 128052
   * 194612
   * 117816
   * 127034
   * 117818
   * 155711
   * 126012
   * 176703
   * 151615
   * 120895
   * 145980
   * 117823
   * 132157
   * 134214
   * 143430
   * 116812
   * 115788
   * 143948
   * 199768
   * 120944
   * 127088
   * 134259
   * 180858
   * 146558
   * 148095
   * 123004
   * 192639
   * 159869
   * 198786
   * 131203
   * 146561
   * 127106
   * 146569
   * 146575
   * 103060
   * 117923
   * 127152
   * 127155
   * 133308
   * 129727
   * 114881
   * 187586
   * 127169
   * 197827
   * 168131
   * 161987
   * 146115
   * 194752
   * 121027
   * 130242
   * 144583
   * 146631
   * 183498
   * 199883
   * 116936
   * 115917
   * 127692
   * 199886
   * 109775
   * 139469
   * 128208
   * 114384
   * 145627
   * 113884
   * 197852
   * 199907
   * 191713
   * 180978
   * 147186
   * 120048
   * 177395
   * 110832
   * 193267
   * 193264
   * 111347
   * 197872
   * 193265
   * 131825
   * 127732
   * 162036
   * 127736
   * 133371
   * 145659
   * 134399
   * 120060
   * 184063
   * 125180
   * 123644
   * 130303
   * 136444
   * 165117
   * 127745
   * 182531
   * 149251
   * 148736
   * 130819
   * 100100
   * 118020
   * 135940
   * 131845
   * 195851
   * 131855
   * 134927
   * 122124
   * 101135
   * 135948
   * 196876
   * 130833
   * 197905
   * 195871
   * 131356
   * 198434
   * 144161
   * 127780
   * 131879
   * 180004
   * 134954
   * 118573
   * 134958
   * 182578
   * 101680
   * 195891
   * 103219
   * 199472
   * 127794
   * 148279
   * 114484
   * 199995
   * 140088
   * 143166
   * 180031
   * 142143
   * 171839
   * 115518
   * 107328
   * 131904
   * 197447
   * 196939
   * 196936
   * 131919
   * 183116
   * 196448
   * 110953
   * 195951
   * 123760
   * 160115
   * 145267
   * 138096
   * 131964
   * 111487
   * 199043
   * 115584
   * 134016
   * 115592
   * 118669
   * 116623
   * 196492
   * 180109
   * 114582
   * 135070
   * 147363
   * 144288
   * 197548
   * 146354
   * 163763
   * 149427
   * 114608
   * 118707
   * 147376
   * 184258
   * 163778
   * 138179
   * 142787
   * 144320
   * 180160
   * 163776
   * 133056
   * 127938
   * 146881
   * 177095
   * 146887
   * 143818
   * 196555
   * 193481
   * 117709
   * 151503
   * 111564
   * 118735
   * 134099
   * 150483
   * 151504
   * 184280
   * 188383
   * 165852
   * 118752
   * 151011
   * 123875
   * 139745
   * 183781
   * 198639
   * 189423
   * 129007
   * 194547
   * 193011
   * 189424
   * 133105
   * 130040
   * 193534
   * 118781
   * 142847
   * 119804
   * 167423
   * 133119
   * 131068
   * 184319
   * 130559
   * 148477
   */
}
