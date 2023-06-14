# LitBall-training
Training data and results for classifiers used in LitBall app

The main classification task in LitBall is to filter academic papers for usage in biocuration. 
There the most useful papers contain evidence from lab or field experiments. 
There is no reason, however, why the methods mentioned in this repo cannot be applied to other paper types that need to be found systematically by LitBall.

### Paper parts subject / key to classification
LitBall will walk the full academic graph (AG) to find papers. 
This means a classification method that uses the **body** of the paper can only be applied to a small part of all papers found by LitBall,
because there is no central server serving the body via API. You may be able to classify papers by using a bulk
of downloaded bodies but there will always be a substantial percentage behind paywalls or just not easily available.

If you decide to use a paper's **abstract** for classification then, at the moment, that seems possible
by using Semantic Scholar's (S2) API. In selected areas of the biomedical literature, around 60% of
abstracts are available. This is not enough for our purpose.

S2 also provides shorter **TL;DR** of abstracts (paper bodies?), in the same way as just mentioned. 
The percentage of papers with TL;DRs is >90%.

So the most complete coverage will be had by classifying paper **titles**. This will also be the most
cost-effective method, and it will allow to leverage large language model APIs.
Classifying abstracts is about 10x as expensive as titles. However, the most effective method overall
seems to be using the TL;DRs as the contain more information than titles alone, and can be had on nearly
every article.

### Large Language Models (LLM) for classification of titles
The original GPT-3 base models (davinci, curie, ada, and babbage) are currently the only GPT models 
that are available to fine-tune. The docs are at https://platform.openai.com/docs/guides/fine-tuning.

Pricing for training goes from $0.0004 / 1K token to $0.03 / 1K token; usage from $0.0016 to $0.12 per 1K.
Training with 5K titles of 20 words ~130K tokens will cost about $0.05 to $40.
Usage in one LitBall query of 10K titles of 20 words ~260K tokens will cost $0.4 to $30 per query.
So the small option looks affordable.

Better than using general LLM is to use domain-specific LLM like BioBERT, BioElectra.

Electra is much more efficent than BERT.

### Baseline measurements

To decide if usage of LLM is really necessary we try to solve the same task using Decision Trees / 
Random Forests.

### Setting up training

#### The Tagger
This app will 
- import files containing DOIs and create files with paper lists in JSON format
- let you eyeball, tag and extract/move/remove/export entries from the files
- export will prepare data for training

#### Data Prep
We use a combination of processes from the CoreNLP Java library (called from Tagger during export),
and TensorFlow Keras TextVectorization API (part of training workflow in Python):
https://www.tensorflow.org/api_docs/python/tf/keras/layers/TextVectorization

CoreNLP:
- lemmatize (replace)
- from Basic Dependencies extract compound n-grams, fuse them using a special character (not SPACE)
- remove DT, IN, TO, CC, PRP$ words

Keras TextVectorization:
- use splitting on SPACE
- remove interpunction
- use word 1-grams + bigrams




-


