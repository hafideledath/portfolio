#include "helpers.h"
#include <math.h>
#include <stdlib.h>
#include <stdio.h>

// Declare all the helper functions
RGBTRIPLE calc_avg(int height, int width, RGBTRIPLE org[height][width], int r, int c);
long *calc_x_edges(int height, int width, RGBTRIPLE org[height][width], int r, int c);
long *calc_y_edges(int height, int width, RGBTRIPLE org[height][width], int r, int c);

// Convert image to grayscale
void grayscale(int height, int width, RGBTRIPLE image[height][width])
{
    // For each row
    for (int r = 0; r < height; r++)
    {
        // For each column
        for (int c = 0; c < width; c++)
        {
            // Calculate the average of each color value and set each RGB value to the average, rounded
            RGBTRIPLE *img = &image[r][c];
            int avg = round(((*img).rgbtRed + (*img).rgbtBlue + (*img).rgbtGreen) / 3.0);
            (*img).rgbtRed = avg, (*img).rgbtBlue = avg, (*img).rgbtGreen = avg;
        }
    }
    return;
}

// Reflect image horizontally
void reflect(int height, int width, RGBTRIPLE image[height][width])
{
    // Declare a variable to refer to the original image
    RGBTRIPLE org[height][width];
    // For each row
    for (int i = 0; i < height; i++)
    {
        // For each column
        for (int j = 0; j < width; j++)
        {
            // Set the pixel value of org to the pixel value of image
            org[i][j] = image[i][j];
        }
    }
    // For each row
    for (int r = 0; r < height; r++)
    {
        // For each column
        for (int c = 0; c < width; c++)
        {
            // Set the value of the pixel in image to the value in org opposite to the original
            image[r][c] = org[r][(width - 1) - c];
        }
    }
    return;
}

// Blur image
void blur(int height, int width, RGBTRIPLE image[height][width])
{
    // Declare a variable to refer to the original image
    RGBTRIPLE org[height][width];
    // For each row
    for (int i = 0; i < height; i++)
    {
        // For each column
        for (int j = 0; j < width; j++)
        {
            // Set the pixel value of org to the pixel value of image
            org[i][j] = image[i][j];
        }
    }
    // For each row
    for (int r = 0; r < height; r++)
    {
        // For each column
        for (int c = 0; c < width; c++)
        {
            // Set the value of the pixel to the average color values of the pixels that create a 9x9 grid around it
            image[r][c] = calc_avg(height, width, org, r, c);
        }
    }
    return;
}

// Calculate the average color values of a pixel based on the pixels that form a 9x9 grid around it
RGBTRIPLE calc_avg(int height, int width, RGBTRIPLE org[height][width], int r, int c)
{
    // Define an array of color values, declare an RGBTRIPLE variable called px and define a float to keep track of the divider
    int colors[3] = {0};
    RGBTRIPLE px;
    float div = 0;
    // For the 9x9 grid surrounding the pixel
    for (int i = -1; i <= 1; i++)
    {
        for (int j = -1; j <= 1; j++)
        {
            // If the pixel is within bounds of the image
            if (r + i >= 0 && c + j >= 0 && r + i < height && c + j < width)
            {
                // Add the values of each color to the respective element in the colors array, and increment the div variable
                RGBTRIPLE new_px = org[r + i][c + j];
                colors[0] += new_px.rgbtRed;
                colors[1] += new_px.rgbtGreen;
                colors[2] += new_px.rgbtBlue;
                div++;
            }
        }
    }
    // Divide the color values by the divider variable and assign them to px
    px.rgbtRed = round(colors[0] / div);
    px.rgbtGreen = round(colors[1] / div);
    px.rgbtBlue = round(colors[2] / div);
    return px;
}

// Detect edges
void edges(int height, int width, RGBTRIPLE image[height][width])
{
    // Declare a variable to refer to the original image
    RGBTRIPLE org[height][width];
    // For each row
    for (int i = 0; i < height; i++)
    {
        // For each column
        for (int j = 0; j < width; j++)
        {
            // Set the pixel value of org to the pixel value of image
            org[i][j] = image[i][j];
        }
    }
    // For each row
    for (int r = 0; r < height; r++)
    {
        // For each column
        for (int c = 0; c < width; c++)
        {
            // Calculate the Gx and Gy values of each pixel using the calc_x_edges and calc_y_edges helper functions
            long *gx = calc_x_edges(height, width, org, r, c);
            long *gy = calc_y_edges(height, width, org, r, c);
            // Calculate each color value by using the sqrt(Gx^2 + Gy^2) formula and round to the nearest integer
            long red_val = round(sqrt(pow(*gx, 2) + pow(*gy, 2)));
            long green_val = round(sqrt(pow(*(gx + 1), 2) + pow(*(gy + 1), 2)));
            long blue_val = round(sqrt(pow(*(gx + 2), 2) + pow(*(gy + 2), 2)));
            // Free the values of Gx and Gy as the helper functions use memory allocation
            free(gx);
            free(gy);
            // Cap the value of each color value at 255, and set the values of the image variable appropriately
            image[r][c].rgbtRed = red_val < 255 ? red_val : 255;
            image[r][c].rgbtGreen = green_val < 255 ? green_val : 255;
            image[r][c].rgbtBlue = blue_val < 255 ? blue_val : 255;
        }
    }
    return;
}

// Calculate the Gx of a pixel based on the pixels that form a 9x9 grid around it
long *calc_x_edges(int height, int width, RGBTRIPLE org[height][width], int r, int c)
{
    // Allocate space for the color values and set the values to 0
    long *colors = malloc(sizeof(long) * 3);
    colors[0] = 0, colors[1] = 0, colors[2] = 0;
    // Declare a variable for the pixel
    RGBTRIPLE px;
    // Create an array for the multipliers for calculating the Gx of the pixel
    int multipliers[] = {-1, 0, 1, -2, 0, 2, -1, 0, 1};
    // For the 9x9 grid surrounding the pixel
    for (int i = -1; i <= 1; i++)
    {
        for (int j = -1; j <= 1; j++)
        {
            // If the pixel is within bounds of the image
            if (r + i >= 0 && c + j >= 0 && r + i < height && c + j < width)
            {
                // Create a variable for the new color values of the pixel
                RGBTRIPLE new_px = org[r + i][c + j];
                // Find the multiplier for the pixel based on its position on the 9x9 grid
                int multiplier = multipliers[3 * (i + 1) + j + 1];
                // Add the value of the pixel multiplied by the multiplier to its respective element in the colors array
                colors[0] += multiplier * new_px.rgbtRed;
                colors[1] += multiplier * new_px.rgbtGreen;
                colors[2] += multiplier * new_px.rgbtBlue;
            }
        }
    }
    return colors;
}

// Calculate the Gy of a pixel based on the pixels that form a 9x9 grid around it
long *calc_y_edges(int height, int width, RGBTRIPLE org[height][width], int r, int c)
{
    // Allocate space for the color values and set the values to 0
    long *colors = malloc(sizeof(long) * 3);
    colors[0] = 0, colors[1] = 0, colors[2] = 0;
    // Declare a variable for the pixel
    RGBTRIPLE px;
    // Create an array for the multipliers for calculating the Gy of the pixel
    int multipliers[] = {-1, -2, -1, 0, 0, 0, 1, 2, 1};
    // For the 9x9 grid surrounding the pixel
    for (int i = -1; i <= 1; i++)
    {
        for (int j = -1; j <= 1; j++)
        {
            // If the pixel is within bounds of the image
            if (r + i >= 0 && c + j >= 0 && r + i < height && c + j < width)
            {
                // Create a variable for the new color values of the pixel
                RGBTRIPLE new_px = org[r + i][c + j];
                // Find the multiplier for the pixel based on its position on the 9x9 grid
                int multiplier = multipliers[3 * (i + 1) + j + 1];
                // Add the value of the pixel multiplied by the multiplier to its respective element in the colors array
                colors[0] += multiplier * new_px.rgbtRed;
                colors[1] += multiplier * new_px.rgbtGreen;
                colors[2] += multiplier * new_px.rgbtBlue;
            }
        }
    }
    return colors;
}