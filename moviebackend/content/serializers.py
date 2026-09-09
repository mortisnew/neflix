

from rest_framework import serializers
from .models import Movie,Series,Season,Episode,Comment,Role,Rating,StreamMovieLink,StreamEpisodeLink



class MovieStreamLinkSerializer(serializers.ModelSerializer):
    class Meta:
        model = StreamMovieLink
        fields = [
            'id',
            'quality',
            'encoder',
            'size_mb',
            'sub_chose',
            'sub_file',
            'url'
        ]


class MovieSerializer(serializers.ModelSerializer):
    movie_link = MovieStreamLinkSerializer(many=True,required=False)
    class Meta:
        model = Movie
        fields = [
                'id',
                'title',
                'poster_url',
                'genre',
                'country',
                'language',
                'description',
                'imdb_rating',
                'duration',
                'release_date',
                'people',
                'movie_link',
                ]


    def create(self, validated_data):
        link_data = validated_data.pop('movie_link',[])
        genre_data = validated_data.pop('genre')
        country_data = validated_data.pop('country')
        language_data = validated_data.pop('language')
        people_data = validated_data.pop('people', [])
        movie = Movie.objects.create(**validated_data)
        movie.genre.set(genre_data)
        movie.country.set(country_data)
        movie.language.set(language_data)
        movie.people.set(people_data)
        for link in link_data:
            StreamMovieLink.objects.create(movie=movie, **link)
        return movie

class EpisodeStreamLinkSerializer(serializers.ModelSerializer):
    class Meta:
        model = StreamEpisodeLink
        fields = [
            'id',
            'quality',
            'encoder',
            'size_mb',
            'sub_chose',
            'sub_file',
            'url'
        ]

class EpisodeSerializer(serializers.ModelSerializer):
    episode_link = EpisodeStreamLinkSerializer(many=True,required=False)
    class Meta:
        model = Episode
        fields = [
            'id',
            'episode_number',
            'episode_link',
        ]

class SeasonSerializer(serializers.ModelSerializer):
    episode_season = EpisodeSerializer(many=True,required=False)
    class Meta:
        model = Season
        fields = [
            'id',
            'season_number',
            'episode_season',
        ]

class SeriesSerializer(serializers.ModelSerializer):
    season_link = SeasonSerializer(many=True,required=False)
    class Meta:
        model = Series
        fields = [
            'id',
            'title',
            'poster_url',
            'genre',
            'country',
            'language',
            'description',
            'imdb_rating',
            'duration',
            'release_date',
            'people',
            'season_link',
        ]
    def create(self, validated_data):
        season_data = validated_data.pop('season_link',[])
        genre_data = validated_data.pop('genre')
        country_data = validated_data.pop('country')
        language_data = validated_data.pop('language')
        people_data = validated_data.pop('people', [])
        series = Series.objects.create(**validated_data)
        series.genre.set(genre_data)
        series.country.set(country_data)
        series.language.set(language_data)
        series.people.set(people_data)


        for season in season_data:
            episode_data = season.pop('episode_season')
            season_obj = Season.objects.create(series=series,**season)
            for episode in episode_data:
                link_data = episode.pop('episode_link')
                episode_obj = Episode.objects.create(season=season_obj,**episode)
                for season_link in link_data:
                    StreamEpisodeLink.objects.create(episode=episode_obj, **season_link)

        return series



class CommentSerializer(serializers.ModelSerializer):
    username = serializers.CharField(source='user.username', read_only=True )
    class Meta:
        model = Comment
        fields = [
        'id',
        'comment',
        'user',
        'username',
        'movie',
        'series',
        ]
        read_only_fields = ('user','username')

    def validate(self, attrs):
        request = self.context.get('request')

        movie = attrs.get('movie')
        series = attrs.get('series')

        if request:
            movie_id = request.query_params.get('movie')
            series_id = request.query_params.get('series')

            if movie is None and movie_id:
                movie = Movie.objects.get(id=movie_id)

            if series is None and series_id:
                series = Series.objects.get(id=series_id)

        if movie is not None and series is not None:
            raise serializers.ValidationError(
                'Movie and series cannot both be selected'
            )

        if movie is None and series is None:
            raise serializers.ValidationError(
                'You must select a movie or series'
            )

        attrs['movie'] = movie
        attrs['series'] = series

        return attrs
        
        
class RoleSerializer(serializers.ModelSerializer):
    class Meta:
        model = Role
        fields = '__all__'

    def validate(self,attrs):
        movie = attrs.get('movie')
        series = attrs.get('series')
        if movie is not None and series is not None:
            raise serializers.ValidationError('Movie and series cannot both be selected')
        if movie is None and series is None:
            raise serializers.ValidationError('You must select a movie or series')
        return attrs
class RatingSerializer(serializers.ModelSerializer):
    class Meta:
        model = Rating
        fields = '__all__'
        read_only_fields = ('user',)

    def validate(self, attrs):
        request = self.context.get('request')

        movie = attrs.get('movie')
        series = attrs.get('series')

        if request:
            movie_id = request.query_params.get('movie')
            series_id = request.query_params.get('series')

            if movie is None and movie_id:
                movie = Movie.objects.get(id=movie_id)

            if series is None and series_id:
                series = Series.objects.get(id=series_id)

        if movie is not None and series is not None:
            raise serializers.ValidationError(
                'Movie and series cannot both be selected'
            )

        if movie is None and series is None:
            raise serializers.ValidationError(
                'You must select a movie or series'
            )

        attrs['movie'] = movie
        attrs['series'] = series

        return attrs
