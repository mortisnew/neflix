from django.db import models
from django.core.exceptions import ValidationError
from accounts.models import ModelUser
from content.models import Movie, Episode, Season,Series


class Favorite(models.Model):
    user = models.ForeignKey(ModelUser, on_delete=models.CASCADE)
    movie = models.ForeignKey(Movie, on_delete=models.CASCADE,blank=True, null=True)
    series = models.ForeignKey(Series, on_delete=models.CASCADE,blank=True, null=True)

    def clean(self):
        if self.movie is not None and self.series is not None:
            raise ValidationError('You can only Select a movie and series')
        if self.movie is None and self.series is None:
            raise ValidationError('You should select a movie and series')

    class Meta:
        constraints = [
            models.UniqueConstraint(
                fields=['user', 'movie', 'series'],
                name='unique_favorite_content'
            )
        ]
    def __str__(self):
        return f'{self.user} - {self.movie} - {self.series}'


class Watchlist(models.Model):
    user = models.ForeignKey(ModelUser, on_delete=models.CASCADE)
    movie = models.ForeignKey(Movie, on_delete=models.CASCADE,blank=True, null=True)
    series = models.ForeignKey(Series, on_delete=models.CASCADE,blank=True, null=True)

    def clean(self):
        if self.movie is not None and self.series is not None:
            raise ValidationError('You can only Select a movie and series')
        if self.movie is None and self.series is None:
            raise ValidationError('You should select a movie and series')



    class Meta:
        constraints = [
            models.UniqueConstraint(
                fields=['user', 'movie', 'series'],
                name='unique_watchlist_content'
            )
        ]
    def __str__(self):
        return f'{self.user} - {self.movie} - {self.series}'


class RecentlyWatched(models.Model):
    user = models.ForeignKey(ModelUser, on_delete=models.CASCADE)
    movie = models.ForeignKey(Movie, on_delete=models.CASCADE,blank=True, null=True,related_name='recently_watched')
    series = models.ForeignKey(Series, on_delete=models.CASCADE,blank=True, null=True,related_name='recently_watched')
    watched_at = models.DateTimeField(auto_now=True)

    def clean(self):
        if self.movie is not None and self.series is not None:
            raise ValidationError('You can only Select a movie and series')
        if self.movie is None and self.series is None:
            raise ValidationError('You should select a movie and series')

    class Meta:
        constraints = [
            models.UniqueConstraint(
                fields=['user', 'movie', 'series'],
                name='unique_recently_watched_content'
            )
        ]
    def __str__(self):
        return f'{self.user} - {self.movie} - {self.series}'

class RecentlyEpisode(models.Model):
    user = models.ForeignKey(ModelUser, on_delete=models.CASCADE)
    episode = models.ForeignKey(Episode, on_delete=models.CASCADE)
    watched_at = models.DateTimeField(auto_now=True)


    class Meta:
        constraints = [
            models.UniqueConstraint(
                fields=['user', 'episode'],
                name='unique_recently_episode'
            )
        ]

    def __str__(self):
        return f'{self.user} - {self.episode} - {self.watched_at}'

class Folder(models.Model):
    title = models.CharField(max_length=100)

    def __str__(self):
        return f'{self.title}'


class FolderItem(models.Model):
    folder = models.ForeignKey(Folder, on_delete=models.CASCADE)
    movie = models.ForeignKey(Movie, on_delete=models.CASCADE,blank=True, null=True)
    series = models.ForeignKey(Series, on_delete=models.CASCADE,blank=True, null=True)

    class Meta:
        constraints = [
            models.UniqueConstraint(
                fields=['folder', 'movie'],
                condition=models.Q(movie__isnull=False)
                , name='unique_item_folder'),
            models.UniqueConstraint(
                fields=['folder', 'series'],
                condition=models.Q(series__isnull=False),
                name='unique_series_content_folder'
            )
        ]

    def clean(self):
        if self.movie is not None and self.series is not None:
            raise ValidationError('You can only Select a movie and series')
        if self.movie is None and self.series is None:
            raise ValidationError('You should select a movie and series')
    def __str__(self):
        return f'{self.folder}'