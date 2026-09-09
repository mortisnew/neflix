from rest_framework import viewsets
from rest_framework.exceptions import ValidationError
from .models import *
from .serializers import (MovieSerializer,SeriesSerializer,
                          CommentSerializer,
                        EpisodeSerializer,RoleSerializer,RatingSerializer,SeasonSerializer,
                          MovieStreamLinkSerializer,EpisodeStreamLinkSerializer)
from rest_framework.permissions import IsAuthenticated
from permissions import OnlyAdmin, IsOwnerOrReadOnly
from rest_framework import filters
from django.db.models import Count
import requests
from rest_framework.decorators import action
from rest_framework.response import Response
from rest_framework import status
from django.http import HttpResponse
from rest_framework.response import Response
from rest_framework import status


class MovieViewSet(viewsets.ModelViewSet):
    permission_classes = [OnlyAdmin]

    queryset = Movie.objects.all()
    serializer_class = MovieSerializer
    filter_backends = (filters.SearchFilter,)
    search_fields = ('title',)

    @action(detail=True, methods=['get'], url_path='poster_url')
    def poster_url(self, request, pk=None):
        movie = self.get_object()

        if not movie.poster_url:
            return Response(
                {"detail": "This person has no avatar."},
                status=status.HTTP_404_NOT_FOUND
            )

        try:
            image_response = requests.get(
                movie.poster_url,
                timeout=15,
                headers={
                    "User-Agent": "Mozilla/5.0"
                }
            )

            if image_response.status_code != 200:
                return Response(
                    {"detail": "Could not fetch poster."},
                    status=status.HTTP_502_BAD_GATEWAY
                )

            content_type = image_response.headers.get(
                "Content-Type",
                "image/jpeg"
            )

            return HttpResponse(
                image_response.content,
                content_type=content_type
            )

        except requests.RequestException:
            return Response(
                {"detail": "poster server is unreachable."},
                status=status.HTTP_502_BAD_GATEWAY
            )
    def get_queryset(self):
        genre = self.request.query_params.get('genre')
        language = self.request.query_params.get('language')
        country = self.request.query_params.get('country')
        sort = self.request.query_params.get('sort')

        queryset = self.queryset

        if genre:
            queryset = queryset.filter(genre=genre)
        if language:
            queryset = queryset.filter(language=language)
        if country:
            queryset = queryset.filter(country=country)
        if sort == 'oldest':
            queryset = queryset.order_by('release_date')
        if sort == 'popular':
            queryset = queryset.annotate(
                    view_count=Count('recently_watched')
                ).order_by('-view_count')
        if sort == 'rating':
            queryset = queryset.order_by('-imdb_rating')
        if sort == 'newest':
            queryset = queryset.order_by('-release_date')
        return queryset


class SeriesViewSet(viewsets.ModelViewSet):
    permission_classes = [OnlyAdmin]

    queryset = Series.objects.all()
    serializer_class = SeriesSerializer
    filter_backends = (filters.SearchFilter,)
    search_fields = ('title',)

    @action(detail=True, methods=['get'], url_path='poster_url')
    def poster_url(self, request, pk=None):
        series = self.get_object()

        if not series.poster_url:
            return Response(
                {"detail": "This person has no avatar."},
                status=status.HTTP_404_NOT_FOUND
            )

        try:
            image_response = requests.get(
                series.poster_url,
                timeout=15,
                headers={
                    "User-Agent": "Mozilla/5.0"
                }
            )

            if image_response.status_code != 200:
                return Response(
                    {"detail": "Could not fetch poster."},
                    status=status.HTTP_502_BAD_GATEWAY
                )

            content_type = image_response.headers.get(
                "Content-Type",
                "image/jpeg"
            )

            return HttpResponse(
                image_response.content,
                content_type=content_type
            )

        except requests.RequestException:
            return Response(
                {"detail": "poster server is unreachable."},
                status=status.HTTP_502_BAD_GATEWAY
            )

    def get_queryset(self):

        genre = self.request.query_params.get('genre')
        language = self.request.query_params.get('language')
        country = self.request.query_params.get('country')
        sort = self.request.query_params.get('sort')

        queryset = self.queryset

        if genre:
            queryset = queryset.filter(genre=genre)
        if language:
            queryset = queryset.filter(language=language)
        if country:
            queryset = queryset.filter(country=country)
        if sort == 'oldest':
                queryset = queryset.order_by('release_date')
        if sort == 'popular':
                queryset = queryset.annotate(
                    view_count=Count('recently_watched')
                ).order_by('-view_count')
        if sort == 'rating':
                queryset = queryset.order_by('-imdb_rating')
        if sort == 'newest':
                queryset = queryset.order_by('-release_date')
        return queryset

class RatingViewSet(viewsets.ModelViewSet):
    permission_classes = [IsAuthenticated]

    queryset = Rating.objects.all()
    serializer_class = RatingSerializer

    def create(self, request, *args, **kwargs):
        serializer = self.get_serializer(data=request.data)
        serializer.is_valid(raise_exception=True)

        movie = serializer.validated_data.get('movie')
        series = serializer.validated_data.get('series')
        rating_value = serializer.validated_data.get('rating')

        if movie is not None:
            existing = Rating.objects.filter(
                user=request.user,
                movie=movie
            ).first()
        else:
            existing = Rating.objects.filter(
                user=request.user,
                series=series
            ).first()

        if existing:
            existing.rating = rating_value
            existing.save()

            return Response(
                self.get_serializer(existing).data,
                status=status.HTTP_200_OK
            )

        rating = serializer.save(user=request.user)

        return Response(
            self.get_serializer(rating).data,
            status=status.HTTP_201_CREATED
        )

    def get_queryset(self):
        return Rating.objects.filter(user=self.request.user)

class SeasonViewSet(viewsets.ModelViewSet):
    permission_classes = [OnlyAdmin]

    queryset = Season.objects.all()
    serializer_class = SeasonSerializer
    def get_queryset(self):
        queryset = self.queryset

        series_id = self.request.query_params.get('series')
        if series_id:
            queryset = queryset.filter(series=series_id)
        return queryset
    def perform_create(self, serializer):
        series_id = self.request.data.get('series')

        if not series_id:
            raise ValidationError('series is required')

        serializer.save(series_id=int(series_id))

class EpisodeViewSet(viewsets.ModelViewSet):
    permission_classes = [OnlyAdmin]

    queryset = Episode.objects.all()
    serializer_class = EpisodeSerializer

    def perform_create(self, serializer):
        season_id = self.request.data.get('season')

        if not season_id:
            raise ValidationError('season is required')

        serializer.save(season_id=int(season_id))

class RoleViewSet(viewsets.ModelViewSet):
    permission_classes = [OnlyAdmin]

    queryset = Role.objects.all()
    serializer_class = RoleSerializer


class CommentViewSet(viewsets.ModelViewSet):
    permission_classes = [IsAuthenticated,IsOwnerOrReadOnly]

    queryset = Comment.objects.all()
    serializer_class = CommentSerializer

    def perform_create(self, serializer):
            movie = self.request.query_params.get('movie')
            series = self.request.query_params.get('series')

            if movie and series:
                raise ValidationError('Cannot specify both movie and series')

            if not movie and not series:
                raise ValidationError('You must specify either movie or series')

            if movie:
                serializer.save(
                    user=self.request.user,
                    movie_id=movie
                )
            else:
                serializer.save(
                    user=self.request.user,
                    series_id=series)
    def get_queryset(self):
        movie = self.request.query_params.get('movie')
        series = self.request.query_params.get('series')

        if movie and series:
            raise ValidationError('Cannot specify both movie and series')
        if movie:
            return Comment.objects.filter(movie=movie)
        if series:
            return Comment.objects.filter(series=series)
        if movie is None and series is None:
            raise ValidationError('you must specify either movie or series')


class StreamMovieLinkViewSet(viewsets.ModelViewSet):
    permission_classes = [OnlyAdmin]
    queryset = StreamMovieLink.objects.all()
    serializer_class = MovieStreamLinkSerializer

    def perform_create(self, serializer):
        movie_id = self.request.data.get('movie')

        if not movie_id:
            raise ValidationError('movie is required')
        serializer.save(movie_id=int(movie_id))

class StreamEpisodeLinkViewSet(viewsets.ModelViewSet):
    permission_classes = [OnlyAdmin]
    queryset = StreamEpisodeLink.objects.all()
    serializer_class = EpisodeStreamLinkSerializer

    def perform_create(self, serializer):
        episode_id = self.request.data.get('episode')

        if not episode_id:
            raise ValidationError('episode is required')
        serializer.save(episode_id=int(episode_id))

