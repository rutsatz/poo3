package com.poo3.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.poo3.service.TwitterService;

import twitter4j.DirectMessage;
import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.Trend;
import twitter4j.Trends;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.User;
import twitter4j.UserList;
import twitter4j.UserStreamListener;
import twitter4j.auth.AccessToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

@Component
public class TwitterIntegration {

	@Autowired
	public TwitterService twitterService;

	ConfigurationBuilder builder;
	Configuration configuration;
	TwitterFactory factory;
	Twitter twitter;
	AccessToken accessToken;
	Trends trends;
	UserStreamListener listener;
	TwitterStream twitterStream;

	private final String CONSUMER_KEY = "9GTBzy5XkVi2Qj6oeRL3hgDFC";
	private final String CONSUMER_SECRET = "B9G4oyGZFlHXodax1CIm8X59eW2NRl8L14mnhgekUmRcjPHaii";

	private final String TOKEN = "990796905260216322-iBBnYUKRfVBVH0sBy2obRDvX6RMPs0I";
	private final String TOKEN_SECRET = "u5fWe9Z27oU5sWBiBlAqODojsKjCjE4aVYRu4aW1Xkeec";

	public TwitterIntegration() {
		try {
			builder = new ConfigurationBuilder();

			builder.setOAuthConsumerKey(CONSUMER_KEY);
			builder.setOAuthConsumerSecret(CONSUMER_SECRET);

			configuration = builder.build();

			factory = new TwitterFactory(configuration);
			twitter = factory.getInstance();

			accessToken = new AccessToken(TOKEN, TOKEN_SECRET);

			twitter.setOAuthAccessToken(accessToken);

			listener = new UserStreamListener() {
				@Override
				public void onStatus(Status status) {
					try {
						System.out.println("onStatus @" + status.getUser().getScreenName() + " - " + status.getText());
						// Se for um usuário diferente que está postando.
						if (!status.getUser().getScreenName().equals(twitter.getScreenName())) {
							twitterService.processRequest(status);
						}
					} catch (IllegalStateException | TwitterException e) {
						e.printStackTrace();
					}
				}

				@Override
				public void onDirectMessage(DirectMessage directMessage) {
					System.out.println("onDirectMessage text:" + directMessage.getText());
					twitterService.processRequest(directMessage);
				}

				@Override
				public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {
					System.out.println("Got a status deletion notice id:" + statusDeletionNotice.getStatusId());
				}

				@Override
				public void onDeletionNotice(long directMessageId, long userId) {
					System.out.println("Got a direct message deletion notice id:" + directMessageId);
				}

				@Override
				public void onTrackLimitationNotice(int numberOfLimitedStatuses) {
					System.out.println("Got a track limitation notice:" + numberOfLimitedStatuses);
				}

				@Override
				public void onScrubGeo(long userId, long upToStatusId) {
					System.out.println("Got scrub_geo event userId:" + userId + " upToStatusId:" + upToStatusId);
				}

				@Override
				public void onStallWarning(StallWarning warning) {
					System.out.println("Got stall warning:" + warning);
				}

				@Override
				public void onFriendList(long[] friendIds) {
					System.out.print("onFriendList");
					for (long friendId : friendIds) {
						System.out.print(" " + friendId);
					}
					System.out.println();
				}

				@Override
				public void onFavorite(User source, User target, Status favoritedStatus) {
					System.out.println(
							"onFavorite source:@" + source.getScreenName() + " target:@" + target.getScreenName() + " @"
									+ favoritedStatus.getUser().getScreenName() + " - " + favoritedStatus.getText());
				}

				@Override
				public void onUnfavorite(User source, User target, Status unfavoritedStatus) {
					System.out.println("onUnFavorite source:@" + source.getScreenName() + " target:@"
							+ target.getScreenName() + " @" + unfavoritedStatus.getUser().getScreenName() + " - "
							+ unfavoritedStatus.getText());
				}

				@Override
				public void onFollow(User source, User followedUser) {
					System.out.println(
							"onFollow source:@" + source.getScreenName() + " target:@" + followedUser.getScreenName());
				}

				@Override
				public void onUnfollow(User source, User followedUser) {
					System.out.println(
							"onFollow source:@" + source.getScreenName() + " target:@" + followedUser.getScreenName());
				}

				@Override
				public void onUserListMemberAddition(User addedMember, User listOwner, UserList list) {
					System.out.println("onUserListMemberAddition added member:@" + addedMember.getScreenName()
							+ " listOwner:@" + listOwner.getScreenName() + " list:" + list.getName());
				}

				@Override
				public void onUserListMemberDeletion(User deletedMember, User listOwner, UserList list) {
					System.out.println("onUserListMemberDeleted deleted member:@" + deletedMember.getScreenName()
							+ " listOwner:@" + listOwner.getScreenName() + " list:" + list.getName());
				}

				@Override
				public void onUserListSubscription(User subscriber, User listOwner, UserList list) {
					System.out.println("onUserListSubscribed subscriber:@" + subscriber.getScreenName() + " listOwner:@"
							+ listOwner.getScreenName() + " list:" + list.getName());
				}

				@Override
				public void onUserListUnsubscription(User subscriber, User listOwner, UserList list) {
					System.out.println("onUserListUnsubscribed subscriber:@" + subscriber.getScreenName()
							+ " listOwner:@" + listOwner.getScreenName() + " list:" + list.getName());
				}

				@Override
				public void onUserListCreation(User listOwner, UserList list) {
					System.out.println(
							"onUserListCreated  listOwner:@" + listOwner.getScreenName() + " list:" + list.getName());
				}

				@Override
				public void onUserListUpdate(User listOwner, UserList list) {
					System.out.println(
							"onUserListUpdated  listOwner:@" + listOwner.getScreenName() + " list:" + list.getName());
				}

				@Override
				public void onUserListDeletion(User listOwner, UserList list) {
					System.out.println(
							"onUserListDestroyed  listOwner:@" + listOwner.getScreenName() + " list:" + list.getName());
				}

				@Override
				public void onUserProfileUpdate(User updatedUser) {
					System.out.println("onUserProfileUpdated user:@" + updatedUser.getScreenName());
				}

				@Override
				public void onUserDeletion(long deletedUser) {
					System.out.println("onUserDeletion user:@" + deletedUser);
				}

				@Override
				public void onUserSuspension(long suspendedUser) {
					System.out.println("onUserSuspension user:@" + suspendedUser);
				}

				@Override
				public void onBlock(User source, User blockedUser) {
					System.out.println(
							"onBlock source:@" + source.getScreenName() + " target:@" + blockedUser.getScreenName());
				}

				@Override
				public void onUnblock(User source, User unblockedUser) {
					System.out.println("onUnblock source:@" + source.getScreenName() + " target:@"
							+ unblockedUser.getScreenName());
				}

				@Override
				public void onRetweetedRetweet(User source, User target, Status retweetedStatus) {
					System.out.println("onRetweetedRetweet source:@" + source.getScreenName() + " target:@"
							+ target.getScreenName() + retweetedStatus.getUser().getScreenName() + " - "
							+ retweetedStatus.getText());
				}

				@Override
				public void onFavoritedRetweet(User source, User target, Status favoritedRetweet) {
					System.out.println("onFavroitedRetweet source:@" + source.getScreenName() + " target:@"
							+ target.getScreenName() + favoritedRetweet.getUser().getScreenName() + " - "
							+ favoritedRetweet.getText());
				}

				@Override
				public void onQuotedTweet(User source, User target, Status quotingTweet) {
					System.out.println("onQuotedTweet" + source.getScreenName() + " target:@" + target.getScreenName()
							+ quotingTweet.getUser().getScreenName() + " - " + quotingTweet.getText());
				}

				@Override
				public void onException(Exception ex) {
					ex.printStackTrace();
					System.out.println("onException:" + ex.getMessage());
				}

			};
			twitterStream = new TwitterStreamFactory().getInstance(twitter.getAuthorization());
			twitterStream.addListener(listener);
			twitterStream.user();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void postTweet(String latestStatus) {

		Status status;
		try {
			// System.out.println(getTrends());
			status = twitter.updateStatus(latestStatus);
			System.out.println("Successfully updated the status to [" + status.getText() + "].");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void sendDirect(long recipientId, String text) throws TwitterException {

		// Twitter sender = TwitterFactory.getSingleton();
		DirectMessage message = twitter.sendDirectMessage(recipientId, text);
		System.out.println("Sent: " + message.getText() + " to @" + message.getRecipientScreenName());

	}

	/**
	 * Obtém as 10 top trends mundiais.
	 * 
	 * @return
	 */
	public String getTrends() {

		try {
			/**
			 * Trends são por lugares. O parâmetro 1 indica que quero as worlwide trends.
			 */
			trends = twitter.getPlaceTrends(1);
		} catch (TwitterException e) {
			e.printStackTrace();
		}
		return this.toString();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();

		int count = 0;
		for (Trend trend : trends.getTrends()) {
			if (count < 10) {
				sb.append(trend.getName() + "\n");
				count++;
			}
		}
		return sb.toString();
	}
}