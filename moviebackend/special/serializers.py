from rest_framework import serializers
from .models import Favorite, Watchlist, RecentlyWatched, RecentlyEpisode, Folder, FolderItem


class FavoriteSerializer(serializers.ModelSerializer):
    class Meta:
        model = Favorite
        fields = '__all__'
        read_only_fields = ('user',)


class WatchlistSerializer(serializers.ModelSerializer):
    class Meta:
        model = Watchlist
        fields = '__all__'
        read_only_fields = ('user',)


class RecentlyWatchedSerializer(serializers.ModelSerializer):
    class Meta:
        model = RecentlyWatched
        fields = '__all__'
        read_only_fields = ('user',)


class RecentlyEpisodeSerializer(serializers.ModelSerializer):
    class Meta:
        model = RecentlyEpisode
        fields = '__all__'
        read_only_fields = ('user',)

class FolderSerializer(serializers.ModelSerializer):
    class Meta:
        model = Folder
        fields = '__all__'


class FolderItemSerializer(serializers.ModelSerializer):
    class Meta:
        model = FolderItem
        fields = '__all__'

    def validate(self, attrs):
        movie = attrs.get('movie')
        series = attrs.get('series')

        if movie is not None and series is not None:
            raise serializers.ValidationError('You can only select a movie or series')
        if movie is None and series is None:
            raise serializers.ValidationError('Movie and series cannot both be null')

        return attrs


class SearchSerializer(serializers.Serializer):
    title = serializers.CharField()