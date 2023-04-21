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
by accessing bulk downloads from Semantic Scholar's (S2) Dataset API. They are on amazonAWS, at the 
moment as "100M records in 30 1.8GB files". So, you either download them to a local server for LitBall
usage, or prepare an amazonAWS server. Note the datasets get updated, so the decision about local/cloud
needs to take into account whether you need access to the newest abstracts. Certainly LitBall will
find the newest papers and then needs the abstracts for classification.

S2 also provides shorter **TL;DR** of abstracts (paper bodies?), in the same way as just mentioned. The latest number
of TL;DRs I read about was "50M". But 100M abstracts are also only 50% of the available 200M papers
they index. Note it is unclear to me how the 50% are distributed---presumably the percentage will go
down with older papers.

So the most complete coverage will be had by classifying paper **titles**. This will also be the most
cost-effective method, and it will allow you to leverage large language model APIs.
Classifying abstracts is about 10x as expensive as titles.

### Large Language Models (LLM) for classification of titles
The original GPT-3 base models (davinci, curie, ada, and babbage) are currently the only GPT models 
that are available to fine-tune. The docs are at https://platform.openai.com/docs/guides/fine-tuning.

Pricing for training goes from $0.0004 / 1K token to $0.03 / 1K token; usage from $0.0016 to $0.12 per 1K.
Training with 5K titles of 20 words ~130K tokens will cost about $0.05 to $40.
Usage in one LitBall query of 10K titles of 20 words ~260K tokens will cost $0.4 to $30 per query.
So the small option looks affordable.

### Setting up LLM training

#### The Tagger
This app will 
- import files containing DOIs and create files with DOI/Title pairs in JSON format
- let you eyeball and extract/move/remove/export entries from the pair files
