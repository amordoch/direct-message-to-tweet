# Direct message to tweet bot, configured for anonymity
# See license in readme
# Developed by Ariel Mordoch
# Requires the tweepy library. https://github.com/tweepy/tweepy
# Intended to be called by another program/script

# Importing everything seems to be the only way I can get the authentication to work for api calls.
from tweepy import *
from sys import exit
from time import sleep
# OAuth stuff first. Add your API keys below.
consumer_key = ''
consumer_key_secret = ''
access_token = ''
access_token_secret = ''
auth = OAuthHandler(consumer_key, consumer_key_secret)
auth.set_access_token(access_token, access_token_secret)
api = API(auth)
# Authentication test. Debug use only.
# print api.me().name

def tweet(text):
    """Really no need for this function, but I defined it anyway."""
    api.update_status(text)

def send_dm(user, screenname, userid, text):
    """For some reason, tweepy seems to like having all the parameters to work."""
    api.send_direct_message(user, screenname, userid, text)

# Get direct messages and count how many were recieved
direct_messages = api.direct_messages()
dm_count = len(direct_messages)
# If there are none stop wasting CPU cycles
if ( dm_count > 0 ):
    print "%s direct messages found" % dm_count
    print "Parsing direct messages..."
    # Using these later to display the amount of tweets rejected and accepted.
    tweetCount = 0
    rejectAtMention = 0
    rejectNoActivationChar = 0
    # Loop through the messages
    for message in direct_messages:
        # Follow the user who sent the direct message. 
        # api.create_friendship(None, message.sender, message.sender.screen_name, message.recipient)
        # Find our activation character, in this case '&&'
        if( '&amp;&amp;' in message.text ):
            # Since we want anonymity, check if there are any @ mentions. If so, don't tweet
            if ( '@' in message.text ):
                print "Tweet contained @ mention, rejecting..."
                send_dm(message.sender, message.sender.screen_name, message.sender, "I won't tweet anything that contains an @ mention. Please try again.")
                rejectAtMention += 1
            else:
                print "Tweet succesful..."
                tweet( message.text[10:] )
                tweetCount += 1
        else:
            print "Tweet did not contain the activation character"
            send_dm(message.sender, message.sender.screen_name, message.sender, "I didn't understand that! To tweet, reply with '&& (your message here)'.")
            rejectNoActivationChar += 1
        # Delete the direct message when we're done.
        api.destroy_direct_message(message.id)
        # If we've followed anyone, unfollow them
        # api.destroy_friendship(None, message.sender, message.sender.screen_name)
    print "Results:\n"
    print "%s tweets succeeded\n" % tweetCount
    print "%s tweets rejected due to an @ mention\n" % rejectAtMention
    print "%s tweets rejected due to not containing the '&&' character\n" % rejectNoActivationChar
    print "Bot will now terminate. Goodbye!"
    sleep(10)
    exit()
else:
    print "No direct messages were found...terminating"
    sleep(2)
    exit()
