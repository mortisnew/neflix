from django.core.validators import MinValueValidator,MaxValueValidator
from django.db import models
from catalog.models import Country,Language,Quality,Genre
from people.models import Person
from accounts.models import ModelUser
from django.core.exceptions import ValidationError

class SubtitleChose(models.TextChoices):
    ORIGINAL = 'original', 'Original'
    SUBTITLE = 'subtitle', 'Subtitle'
    DUBBED = 'dubbed', 'Dubbed'


class Movie(models.Model):
    title = models.CharField(max_length=100)
    poster_url = models.URLField()
    genre = models.ManyToManyField(Genre)
    country = models.ManyToManyField(Country)
    language = models.ManyToManyField(Language)
    description = models.TextField(blank=True, null=True)
    imdb_rating = models.DecimalField(max_digits=5, decimal_places=2,validators=[MinValueValidator(1), MaxValueValidator(10)])
    duration = models.PositiveIntegerField(blank=True, null=True)
    release_date = models.DateField()
    people = models.ManyToManyField(Person, through='Role',through_fields=('movie','person'))


    def __str__(self):
        return self.title

class StreamMovieLink(models.Model):
    movie = models.ForeignKey(Movie, on_delete=models.CASCADE,related_name='movie_link')
    quality = models.ForeignKey(Quality, on_delete=models.CASCADE)
    encoder = models.CharField(max_length=100)
    size_mb = models.PositiveIntegerField()
    sub_chose = models.CharField(choices=SubtitleChose, max_length=100)
    sub_file = models.FileField(blank=True, null=True)
    url = models.URLField()

    def __str__(self):
        return f'{self.movie} | {self.quality} | {self.encoder}'



class Series(models.Model):
    title = models.CharField(max_length=100)
    poster_url = models.URLField()
    genre = models.ManyToManyField(Genre)
    country = models.ManyToManyField(Country)
    language = models.ManyToManyField(Language)
    description = models.TextField()
    imdb_rating = models.DecimalField(max_digits=5, decimal_places=2,validators=[MinValueValidator(1), MaxValueValidator(10)])
    duration = models.PositiveIntegerField()
    release_date = models.DateField()
    people = models.ManyToManyField(Person, through='Role',through_fields=('series','person'))

    def __str__(self):
        return self.title



class Season(models.Model):
    series = models.ForeignKey(Series, on_delete=models.CASCADE,related_name='season_link')
    season_number = models.IntegerField()

    class Meta:
        constraints = [
            models.UniqueConstraint(
                fields=['series', 'season_number'],
                name='unique_series_season_number'
            )
        ]

    def __str__(self):
        return f'{self.series} | {self.season_number}'


class Episode(models.Model):
    season = models.ForeignKey(Season, on_delete=models.CASCADE,related_name='episode_season')
    episode_number = models.IntegerField()

    class Meta:
        constraints = [
            models.UniqueConstraint(
                fields=['season', 'episode_number'],
                name='unique_season_episode_number'
            )
        ]
    def __str__(self):
        return f'{self.season} | {self.episode_number}'


class StreamEpisodeLink(models.Model):
    episode = models.ForeignKey(Episode, on_delete=models.CASCADE,related_name='episode_link')
    quality = models.ForeignKey(Quality, on_delete=models.CASCADE)
    encoder = models.CharField(max_length=100)
    size_mb = models.PositiveIntegerField()
    sub_chose = models.CharField(choices=SubtitleChose, max_length=100)
    sub_file = models.FileField(blank=True, null=True)
    url = models.URLField()

    def __str__(self):
        return f'{self.episode} | {self.quality} | {self.encoder}'


class PersonType(models.TextChoices):
    ACTOR = 'actor', 'Actor'
    DIRECTOR = 'director', 'Director'
    WRITER = 'writer', 'Writer'

class Role(models.Model):
    person = models.ForeignKey(Person, on_delete=models.CASCADE)
    role_type = models.CharField(choices=PersonType, max_length=100)
    movie = models.ForeignKey(Movie, on_delete=models.CASCADE,blank=True, null=True)
    series = models.ForeignKey(Series, on_delete=models.CASCADE,blank=True, null=True)
    class Meta:
        constraints = [
            models.UniqueConstraint(
                fields=['person', 'movie','series', 'role_type'],
                name='unique_person_content_role'
            )
        ]
    def clean(self):
        if self.movie is not None and self.series is not None:
            raise ValidationError('You can only Choice between Movie and Series')
        if self.movie is None and self.series is None:
            raise ValidationError('You must select a movie or series')

    def __str__(self):
        return f'{self.person} | {self.role_type}'

class Comment(models.Model):
    user = models.ForeignKey(ModelUser, on_delete=models.CASCADE)
    movie = models.ForeignKey(Movie, on_delete=models.CASCADE,blank=True, null=True)
    series = models.ForeignKey(Series, on_delete=models.CASCADE,blank=True, null=True)
    comment = models.TextField()

    def clean(self):
        if self.movie is not None and self.series is not None:
            raise ValidationError('You can only Choice between Movie and Series')
        if self.movie is None and self.series is None:
            raise ValidationError('You must select a movie or series')


    def __str__(self):
        return f'{self.user} | {self.comment}'

class Rating(models.Model):
    user = models.ForeignKey(ModelUser, on_delete=models.CASCADE)
    rating = models.IntegerField(validators=[MinValueValidator(1), MaxValueValidator(5)])
    movie = models.ForeignKey(Movie, on_delete=models.CASCADE, blank=True, null=True)
    series = models.ForeignKey(Series, on_delete=models.CASCADE, blank=True, null=True)

    def clean(self):
        if self.movie is not None and self.series is not None:
            raise ValidationError('You can only Choice between Movie and Series')
        if self.movie is None and self.series is None:
            raise ValidationError('You must select a movie or series')


    class Meta:
            constraints = [
                models.UniqueConstraint(
                    fields=['user','movie'],
                    condition=models.Q(movie__isnull=False)
                    ,name='unique_user_content_rating'),
                models.UniqueConstraint(
                    fields=['user','series'],
                    condition=models.Q(series__isnull=False),
                    name='unique_series_content_rating'
                )
            ]

    def __str__(self):
        return f'{self.user} | {self.rating}'


