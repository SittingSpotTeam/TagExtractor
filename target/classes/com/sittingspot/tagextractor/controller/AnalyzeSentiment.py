import sys
import vaderSentiment.vaderSentiment
from vaderSentiment.vaderSentiment import SentimentIntensityAnalyzer

analyzer = SentimentIntensityAnalyzer()
review = sys.argv[1]
sentiment = analyzer.polarity_scores(review)
print(sentiment)